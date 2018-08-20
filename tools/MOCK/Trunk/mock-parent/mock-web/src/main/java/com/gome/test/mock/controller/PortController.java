package com.gome.test.mock.controller;

import com.gome.test.mock.dao.PortDao;
import com.gome.test.mock.model.Result;
import com.gome.test.mock.model.TableResult;
import com.gome.test.mock.model.bean.Port;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2015/10/20.
 */

@Controller
@ComponentScan
@RequestMapping(value = "/port")
public class PortController {
    @Autowired
    private PortDao portDao;
    @RequestMapping(value = "/getPortForDrop", method = RequestMethod.GET)
    @ResponseBody
    public Result getHostData() {
        try {

            return new Result(portDao.getAll());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }


    @RequestMapping(value = "/searchAll")
    @ResponseBody
    TableResult getAllHosts() {
        try {

            List hostList=portDao.getAll();

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
    public Result portsDetail (
            @RequestParam(value = "port_id", required = true) int port_id
    ) {
        try {
            Port port = portDao.get(port_id);
            return new Result(port);
        }catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result portsDelete (
            @RequestParam(value = "port_id", required = true) int port_id
    ) {
        try {
            portDao.delete(port_id);
            return new Result(false);
        }catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }


    @RequestMapping(value = "/search")
    public ModelAndView getApiPage() {
        return new ModelAndView("ports");
    }


    @RequestMapping(value = "/search/{port_id}")
    public ModelAndView hostModifyView(@PathVariable(value = "port_id") int port_id){
        ModelAndView mv=new ModelAndView("ports");
        mv.addObject("view_port_id",port_id);
        return mv;

    }


    @RequestMapping(value = "/checkNumber", method = RequestMethod.GET)
    @ResponseBody
    public Result checkName(
            @RequestParam(value = "port_number", required = true) int portnumber,
            @RequestParam(value = "port_id", required = true) int port_id
    )
    {
        try {
            List<Object[]>  portsList = portDao.queryPortListByNumber(portnumber);
            Result result = new Result();
            if (portsList.size() == 0)
                result.setIsError(false);
            else if (portsList.size() > 1)
                result.setIsError(true);
            else {
                result.setIsError(!portsList.get(0)[0].equals(port_id));
            }
            return result;
        }catch (Exception ex)
        {
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Result savePort(
            @RequestParam(value = "port_number", required = true) int port_number,
            @RequestParam(value = "enable", required = true) String enable
    ) {
        try {
            Result result=new Result();
            //验证端口是否可用
            List<Object[]>  portList =portDao.queryPortListByNumber(port_number);
            if(portList==null || portList.size()==0) {
                Port port=new Port();
                port.setEnable(Short.valueOf(enable));
                port.setPortNumber(port_number);
                portDao.save(port);
                result.setIsError(false);
            }else {
                result.setIsError(true);
                result.setMessage(String.format("端口 ‘%s’已经存在！",port_number));
            }

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }


    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    @ResponseBody
    public Result editPort(
            @RequestParam(value = "port_number", required = true) int port_number,
            @RequestParam(value = "enable", required = true) String enable,
            @RequestParam(value = "port_id", required = true) int port_id
    ) {
        try {
            Result result=new Result();
            //验证端口是否可用
            List<Object[]>  portList =portDao.queryPortListByNumber(port_number);
            if(portList==null || portList.size()==0 || (portList.size()==1 && portList.get(0)[0].equals(port_id))) {
                Port port=new Port();
                port.setEnable(Short.valueOf(enable));
                port.setPortNumber(port_number);
                port.setId(port_id);
                portDao.update(port);
                result.setIsError(false);
            }else {
                result.setIsError(true);
                result.setMessage(String.format("保存失败，端口 ‘%s’已经存在！",port_number));
            }

            return result;

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }

}
