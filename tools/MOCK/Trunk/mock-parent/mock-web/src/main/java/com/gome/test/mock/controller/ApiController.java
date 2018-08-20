package com.gome.test.mock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gome.test.api.model.MenuDescriptor;
import com.gome.test.mock.bo.MenuBo;
import com.gome.test.mock.dao.ApiDao;
import com.gome.test.mock.dao.HostDao;
import com.gome.test.mock.dao.TemplateDao;
import com.gome.test.mock.model.Result;
import com.gome.test.mock.model.TableResult;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.Host;
import com.gome.test.mock.model.bean.Template;

/**
 * Created by zhangjiadi on 15/10/15.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/api")
public class ApiController {

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private HostDao hostDao;

    @Autowired
    private MenuBo menuBo;

    @Autowired
    private TemplateDao templateDao;

    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    @ResponseBody
    public Result checkName(@RequestParam(value = "apiname", required = true) String apiname, @RequestParam(value = "api_id", required = true) int api_id) {
        try {
            List<Object[]> apiList = this.apiDao.queryApiListByApiName(apiname);
            Result result = new Result();
            if (apiList.size() == 0) {
                result.setIsError(false);
            } else if (apiList.size() > 1) {
                result.setIsError(true);
            } else {

                result.setIsError(!apiList.get(0)[0].equals(api_id));
            }
            return result;
        } catch (Exception ex) {
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Result saveApi(@RequestParam(value = "api_name", required = true) String api_name, @RequestParam(value = "host_id", required = true) String host_id, @RequestParam(value = "template_id", required = true) String template_id, @RequestParam(value = "key_words", required = true) String key_words, @RequestParam(value = "intercept_param", required = false) String intercept_param, @RequestParam(value = "descript", required = true) String descript, @RequestParam(value = "enabel", required = true) String enabel, @RequestParam(value = "api_id", required = true) int api_id) {
        try {
            Result result = new Result();
            //验证名称是否可用
            List<Object[]> apiList = this.apiDao.queryApiListByApiName(api_name);
            if (apiList == null || apiList.size() == 0) {

                Api api = new Api();
                api.setApiName(api_name);
                api.setDescript(descript);
                api.setEnable(Short.valueOf(enabel));
                api.setHostId(Integer.valueOf(host_id));
                api.setTemplateId(Integer.valueOf(template_id));
                api.setKeyWords(key_words);
                api.setInterceptParam(intercept_param);
                this.apiDao.save(api);
                result.setIsError(false);
            } else {
                result.setIsError(true);
                result.setMessage(String.format("服务‘%s’已经存在！", api_name));
            }

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Result getApi(@RequestParam(value = "api_id", required = true) String api_id) {
        try {

            Api api = this.apiDao.get(Integer.valueOf(api_id));
            return new Result(api);

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    @ResponseBody
    public Result editApi(@RequestParam(value = "api_name", required = true) String api_name, @RequestParam(value = "host_id", required = true) String host_id, @RequestParam(value = "template_id", required = true) String template_id, @RequestParam(value = "key_words", required = true) String key_words, @RequestParam(value = "intercept_param", required = false) String intercept_param, @RequestParam(value = "descript", required = true) String descript, @RequestParam(value = "enabel", required = true) String enable, @RequestParam(value = "api_id", required = true) int api_id) {
        try {
            Result result = new Result();
            //验证名称是否可用
            List<Object[]> apiList = this.apiDao.queryApiListByApiName(api_name);
            if (apiList == null || apiList.size() == 0 || (apiList.size() == 1 && apiList.get(0)[0].equals(api_id))) {
                Api api = new Api();
                api.setApiName(api_name);
                api.setId(api_id);
                api.setDescript(descript);
                api.setEnable(Short.valueOf(enable));
                api.setHostId(Integer.valueOf(host_id));
                api.setTemplateId(Integer.valueOf(template_id));
                api.setKeyWords(key_words);
                api.setInterceptParam(intercept_param);
                this.apiDao.updateApiByIdExceptWorkFlow(api);
                result.setIsError(false);
            } else {
                result.setIsError(true);
                result.setMessage(String.format("服务‘%s’已经存在！", api_name));
            }

            return result;
        } catch (Exception ex) {
            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public Result getAllApi() {
        try {
            return new Result(this.apiDao.getApiList());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/search")
    public ModelAndView getApiPage() {
        return new ModelAndView("apis");
    }

    @RequestMapping(value = "/searchAll")
    @ResponseBody
    TableResult getAllApis() {
        try {
            List apiList = this.apiDao.queryApiListBySearch();
            if (apiList != null) {
                return new TableResult(false, 1, apiList.size(), apiList.size(), apiList.toArray());
            } else {
                return new TableResult();
            }
        } catch (Exception ex) {
            return new TableResult(true, ex.toString());
        }
    }

    @RequestMapping(value = "/apiAdd")
    public ModelAndView apiModify() {
        ModelAndView mv = new ModelAndView("apiModify");
        mv.addObject("api_types", "create");
        mv.addObject("api_id", "0");
        return mv;

    }

    @RequestMapping(value = "/apiEdit/{api_id}")
    public ModelAndView apiModifyEdit(@PathVariable(value = "api_id") int api_id) {
        ModelAndView mv = new ModelAndView("apiModify");
        mv.addObject("api_types", "edit");
        mv.addObject("api_id", api_id);
        return mv;

    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result apisDetail(@RequestParam(value = "api_id", required = true) int api_id) {
        try {
            Object[] api = this.apiDao.getApiDetailById(api_id);
            Host host = this.hostDao.get(Integer.valueOf(api[1].toString()));
            Template template = this.templateDao.get(Integer.valueOf(api[2].toString()));
            api[1] = String.format("%s_%s_%s", host.getServiceName(), host.getDomain(), host.getUrl());
            if (template != null) {
                api[2] = String.format("%s", template.getTemplateName());
            }
            return new Result(api);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/apiView/{api_id}")
    public ModelAndView apiModifyView(@PathVariable(value = "api_id") int api_id) {
        ModelAndView mv = new ModelAndView("apiModify");
        mv.addObject("api_types", "view");
        mv.addObject("api_id", api_id);
        return mv;

    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result apisDelete(@RequestParam(value = "api_id", required = true) int api_id) {
        try {
            this.apiDao.delete(api_id);
            return new Result(false);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/workflow/{api_id}")
    public ModelAndView apiWorkFlowEdit(@PathVariable(value = "api_id") int api_id) {
        ModelAndView mv = new ModelAndView("workFlowModify");
        Api api = this.apiDao.get(api_id);
        if (api != null) {
            mv.addObject("api_id", api.getId());
            mv.addObject("api_name", api.getApiName());
            mv.addObject("api_descript", api.getDescript());
            mv.addObject("intercept_param", api.getInterceptParam());
            mv.addObject("api_enable", api.getEnable());
            mv.addObject("api_flowcontent", api.getFlowContent());
            mv.addObject("api_keywords", api.getKeyWords());
            mv.addObject("api_hostid", api.getHostId());
            mv.addObject("api_templateid", api.getTemplateId());
            mv.addObject("api_nameStr", api.getApiName() + (api.getEnable() == 1 ? "" : "(无效)"));
            Host host = this.hostDao.get(api.getHostId());
            Template template = this.templateDao.get(api.getTemplateId());
            if (host != null) {
                mv.addObject("api_host_name", host.getServiceName() + (host.getEnable() == 1 ? "" : "(无效)"));
                mv.addObject("api_host_domain", host.getDomain());
                mv.addObject("api_host_url", host.getUrl());
                mv.addObject("api_host_enable", host.getEnable());
            }
            if (template != null) {
                mv.addObject("api_template_name", template.getTemplateName() + (template.getEnable() == 1 ? "" : "(无效)"));
            }
        }

        return mv;

    }

    @RequestMapping(value = "/workflow/query")
    @ResponseBody
    public Result workFlowDetail(@RequestParam(value = "stepClass", required = true) String stepClass) {
        try {
            List<MenuDescriptor> list = this.menuBo.getStepMenu(stepClass);
            return new Result(list);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/save/workflow", method = RequestMethod.GET)
    @ResponseBody
    public Result saveFlowContent(@RequestParam(value = "flow_content", required = true) String flow_content, @RequestParam(value = "api_id", required = true) int api_id) {
        try {
            Result result = new Result();
            this.apiDao.updateWorkFlowById(api_id, flow_content);
            result.setIsError(false);

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

}
