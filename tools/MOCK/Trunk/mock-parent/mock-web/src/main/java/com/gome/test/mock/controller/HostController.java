package com.gome.test.mock.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gome.test.mock.cnst.HostTypeConst;
import com.gome.test.mock.cnst.ProtocolConst;
import com.gome.test.mock.dao.HostDao;
import com.gome.test.mock.dao.PortDao;
import com.gome.test.mock.model.Result;
import com.gome.test.mock.model.TableResult;
import com.gome.test.mock.model.bean.Host;
import com.gome.test.mock.model.bean.Port;

/**
 * Created by zhangjiadi on 15/10/17.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/host")
public class HostController {

    @Autowired
    private HostDao hostDao;

    @Autowired
    private PortDao portDao;

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Result saveHost(@RequestParam(value = "host_name", required = true) String service_name, @RequestParam(value = "host_domain", required = true) String domain, @RequestParam(value = "host_url", required = true) String url, @RequestParam(value = "host_port", required = true) int port_id, @RequestParam(value = "enabel", required = true) String enabel, @RequestParam(value = "host_hostType", required = true) String host_type, @RequestParam(value = "host_protocoltype", required = true) String protocolType) {
        try {
            Result result = new Result();
            Host host = new Host();
            host.setEnable(Short.valueOf(enabel));
            host.setDomain(domain);
            host.setHostType(Short.valueOf(host_type));
            host.setPortId(port_id);
            host.setProtocolType(Short.valueOf(protocolType));
            host.setServiceName(service_name);
            host.setUrl(url);
            this.hostDao.save(host);
            result.setIsError(false);

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    @ResponseBody
    public Result editHost(@RequestParam(value = "host_name", required = true) String service_name, @RequestParam(value = "host_domain", required = true) String domain, @RequestParam(value = "host_url", required = true) String url, @RequestParam(value = "host_port", required = true) int port_id, @RequestParam(value = "enabel", required = true) String enabel, @RequestParam(value = "host_hostType", required = true) String host_type, @RequestParam(value = "host_protocoltype", required = true) String protocolType, @RequestParam(value = "host_id", required = true) int host_id) {
        try {
            Result result = new Result();
            Host host = new Host();
            host.setEnable(Short.valueOf(enabel));
            host.setDomain(domain);
            host.setHostType(Short.valueOf(host_type));
            host.setPortId(port_id);
            host.setServiceName(service_name);
            host.setProtocolType(Short.valueOf(protocolType));
            host.setUrl(url);
            host.setId(host_id);
            this.hostDao.update(host);
            result.setIsError(false);

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public Result getAllHost() {
        try {
            return new Result(this.hostDao.getAll());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/hostAdd")
    public ModelAndView hostAdd() {
        ModelAndView mv = new ModelAndView("hostModify");
        mv.addObject("host_types", "create");
        mv.addObject("host_id", "0");
        return mv;
    }

    @RequestMapping(value = "/hostEdit/{host_id}")
    public ModelAndView hostModifyEdit(@PathVariable(value = "host_id") int host_id) {
        ModelAndView mv = new ModelAndView("hostModify");
        mv.addObject("host_types", "edit");
        Host host = this.hostDao.get(host_id);
        mv.addObject("host_id", host.getId());
        mv.addObject("enabel", host.getEnable());
        mv.addObject("host_domain", host.getDomain());
        mv.addObject("host_type", host.getHostType());
        mv.addObject("host_port", host.getPortId());
        mv.addObject("host_protocol", host.getProtocolType());
        mv.addObject("service_name", host.getServiceName());
        mv.addObject("host_url", host.getUrl());
        return mv;

    }

    @RequestMapping(value = "/hostView/{host_id}")
    public ModelAndView hostModifyView(@PathVariable(value = "host_id") int host_id) {
        ModelAndView mv = new ModelAndView("hostModify");
        mv.addObject("host_types", "view");
        Host host = this.hostDao.get(host_id);
        mv.addObject("host_id", host.getId());
        mv.addObject("enabel", host.getEnable());
        mv.addObject("host_domain", host.getDomain());
        mv.addObject("host_type", host.getHostType());
        mv.addObject("host_port", host.getPortId());
        mv.addObject("host_protocol", host.getProtocolType());
        mv.addObject("service_name", host.getServiceName());
        mv.addObject("host_url", host.getUrl());
        return mv;

    }

    @RequestMapping(value = "/search")
    public ModelAndView getHostPage() {
        return new ModelAndView("hosts");
    }

    @RequestMapping(value = "/searchAll")
    @ResponseBody
    TableResult getAllHosts() {
        try {

            List hostList = this.hostDao.queryHostListBySearch();

            if (hostList != null) {
                return new TableResult(false, 1, hostList.size(), hostList.size(), hostList.toArray());
            } else {
                return new TableResult();
            }
        } catch (Exception ex) {
            return new TableResult(true, ex.toString());
        }
    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result hostDetail(@RequestParam(value = "host_id", required = true) int host_id) {
        try {
            Object[] host = this.hostDao.getHostDetailById(host_id);
            Port port = this.portDao.get(Integer.valueOf(host[2].toString()));
            host[2] = String.format(" %s ", port.getPortNumber());
            host[6] = ProtocolConst.PROTOCOL_CODES.get(Integer.valueOf(host[6].toString())).toString();
            host[5] = HostTypeConst.HostType_CODES.get(Integer.valueOf(host[5].toString())).toString();
            //hosts[1] = lcdService.getNameByValue(Constant.ENV, Integer.valueOf(hosts[1].toString()));
            return new Result(host);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result hostDelete(@RequestParam(value = "host_id", required = true) int host_id) {
        try {
            this.hostDao.delete(host_id);
            return new Result(false);
        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/getHostForDrop", method = RequestMethod.GET)
    @ResponseBody
    public Result getHostData() {
        try {

            return new Result(this.hostDao.getAll());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/gethostTypeForDrop", method = RequestMethod.GET)
    @ResponseBody
    public Result getHostTypeData() {
        try {
            Map<Integer, String> maps = HostTypeConst.HostType_CODES;
            List<Object[]> result = new ArrayList<>();
            for (int key : maps.keySet()) {
                Object[] objects = new Object[2];
                objects[0] = key;
                objects[1] = maps.get(key);
                result.add(objects);
            }
            return new Result(result);

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/getProtocolTypeForDrop", method = RequestMethod.GET)
    @ResponseBody
    public Result getProtocolTypeData() {
        try {
            Map<Integer, String> maps = ProtocolConst.PROTOCOL_CODES;
            List<Object[]> result = new ArrayList<>();
            for (int key : maps.keySet()) {
                Object[] objects = new Object[2];
                objects[0] = key;
                objects[1] = maps.get(key);
                result.add(objects);
            }
            return new Result(result);

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    @ResponseBody
    public Result checkName(@RequestParam(value = "host_id", required = true) int host_id, @RequestParam(value = "host_name", required = true) String host_name, @RequestParam(value = "host_domain", required = true) String host_domain, @RequestParam(value = "host_url", required = true) String host_url) {
        try {
            List<Object[]> hostsList = this.hostDao.queryApiListByName(host_name, host_domain, host_url);
            Result result = new Result();
            if (hostsList == null) {
                result.setIsError(false);
            } else if (hostsList.size() > 1) {
                result.setIsError(true);
                result.setData(1);
            } else {
                result.setIsError(false);
                if ((int) hostsList.get(0)[0] != host_id) {
                    if (hostsList.get(0)[3].equals(host_name)) {
                        result.setIsError(true);
                        result.setData(2);
                    } else if (hostsList.get(0)[1].equals(host_domain)) {
                        result.setIsError(true);
                        result.setData(3);
                    } else if (hostsList.get(0)[4].equals(host_url)) {
                        result.setIsError(true);
                        result.setData(4);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            return new Result(true, String.valueOf(ex));
        }
    }

}
