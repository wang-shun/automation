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

import com.gome.test.mock.dao.TemplateDao;
import com.gome.test.mock.model.Result;
import com.gome.test.mock.model.TableResult;
import com.gome.test.mock.model.bean.Template;

@Controller
@ComponentScan
@RequestMapping(value = "/template")
public class TemplateController {

    @Autowired
    private TemplateDao templateDao;

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Result saveTemplate(@RequestParam(value = "template_name", required = true) String template_name, @RequestParam(value = "template_content", required = true) String template_content, @RequestParam(value = "enable", required = true) String enable) {
        try {
            Result result = new Result();
            Template template = new Template();
            template.setTemplateName(template_name);
            template.setTemplateContent(template_content);
            template.setEnable(Short.valueOf(enable));
            this.templateDao.save(template);
            result.setIsError(false);
            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    @ResponseBody
    public Result editTemplate(@RequestParam(value = "template_id", required = true) int template_id, @RequestParam(value = "template_name", required = true) String template_name, @RequestParam(value = "template_content", required = true) String template_content, @RequestParam(value = "enable", required = true) String enable) {
        try {
            Result result = new Result();
            Template template = new Template();
            template.setId(template_id);
            template.setEnable(Short.valueOf(enable));
            template.setTemplateName(template_name);
            template.setTemplateContent(template_content);
            this.templateDao.update(template);
            result.setIsError(false);

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result templateDetail(@RequestParam(value = "template_id", required = true) int template_id) {
        try {
            Object[] template = this.templateDao.getTemplateDetailById(template_id);
            return new Result(template);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/search")
    public ModelAndView getTemplatePage() {
        return new ModelAndView("templates");
    }

    @RequestMapping(value = "/searchAll")
    @ResponseBody
    TableResult getAllTemplates() {
        try {
            List templateList = this.templateDao.queryTemplateListBySearch();
            if (templateList != null) {
                return new TableResult(false, 1, templateList.size(), templateList.size(), templateList.toArray());
            } else {
                return new TableResult();
            }
        } catch (Exception ex) {
            return new TableResult(true, ex.toString());
        }
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result templateDelete(@RequestParam(value = "template_id", required = true) int template_id) {
        try {
            this.templateDao.delete(template_id);
            return new Result(false);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/templateAdd")
    public ModelAndView templateModify() {
        ModelAndView mv = new ModelAndView("templateModify");
        mv.addObject("template_types", "create");
        mv.addObject("template_id", "0");
        return mv;

    }

    @RequestMapping(value = "/templateEdit/{template_id}")
    public ModelAndView templateModifyEdit(@PathVariable(value = "template_id") int template_id) {
        ModelAndView mv = new ModelAndView("templateModify");
        mv.addObject("template_types", "edit");
        mv.addObject("template_id", template_id);
        return mv;

    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Result getApi(@RequestParam(value = "template_id", required = true) String template_id) {
        try {

            Template template = this.templateDao.get(Integer.valueOf(template_id));
            return new Result(template);

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/getTemplateForDrop", method = RequestMethod.GET)
    @ResponseBody
    public Result getTemplateData() {
        try {

            return new Result(this.templateDao.getAll());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }
}
