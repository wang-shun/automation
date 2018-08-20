package com.gome.test.mq.controller;

import com.gome.test.mq.bo.*;
import com.gome.test.mq.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjiadi on 15/12/16.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/mq")
public class MQController {
    @Autowired
    private TemplateUtil tmpUtil;

    @RequestMapping(value = "/search")
    public ModelAndView getApiPage() {
        return new ModelAndView("mqs");
    }

    //get
    @RequestMapping(value = "/get/{title}/{pid}/{hostName}/{qmgName}/{channel}/{ccsid}/{port}/{qName}/{qType}/{qisCmd}/mq")
    public ModelAndView hostModifyView(@PathVariable(value = "hostName") String hostName
            , @PathVariable(value = "qmgName") String qmgName
            , @PathVariable(value = "qName") String qName
            , @PathVariable(value = "channel") String channel
            , @PathVariable(value = "ccsid") String ccsid
            , @PathVariable(value = "port") String port
            , @PathVariable(value = "qType") String qType
            , @PathVariable(value = "qisCmd") String qisCmd
            , @PathVariable(value = "title") String title
            , @PathVariable(value = "pid") String pid) {
        ModelAndView mv = new ModelAndView("mqs");

        mv.addObject("mq_hostname", hostName);
        mv.addObject("mq_qmgname", qmgName);
        mv.addObject("mq_channel", channel);
        mv.addObject("mq_ccsid", ccsid);
        mv.addObject("mq_port", port);
        mv.addObject("mq_qname", qName);
        mv.addObject("mq_pid", pid);
        mv.addObject("mq_type", qType);
        mv.addObject("mq_iscmd", qisCmd);
        mv.addObject("title",title);
        return mv;

    }

    @Autowired
    private MQUtil util;
    @Autowired
    private LeftTreeService leftTreeService;

    @RequestMapping(value = "/searchAll")
    @ResponseBody
    Result searchAll(
            @RequestParam(value = "mq_qname") String mq_qname,
            @RequestParam(value = "mq_hostname") String mq_hostname,
            @RequestParam(value = "mq_qmgname") String mq_qmgname,
            @RequestParam(value = "mq_channel") String mq_channel,
            @RequestParam(value = "mq_ccsid") String mq_ccsid,
            @RequestParam(value = "mq_port") String mq_port,
            @RequestParam(value = "mq_pid") String mq_pid

    ) {
        try {

            MQ mq = new MQ(mq_hostname, mq_qmgname, mq_channel, Integer.valueOf(mq_ccsid), Integer.valueOf(mq_port));
            mq.setMqUser(leftTreeService.getMqUser(mq_pid, String.format("%s,%s,%s,%s,%s", mq_hostname, mq_qmgname, mq_channel, mq_ccsid, mq_port)));
            util.set_mq(mq);
            List mqList = util.getAllmessageList(mq_qname);
            return new Result(mqList);

        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }

    @RequestMapping(value = "/add")
    @ResponseBody
    Result add(
            @RequestParam(value = "mq_qname") String mq_qname,
            @RequestParam(value = "mq_hostname") String mq_hostname,
            @RequestParam(value = "mq_qmgname") String mq_qmgname,
            @RequestParam(value = "mq_channel") String mq_channel,
            @RequestParam(value = "mq_ccsid") String mq_ccsid,
            @RequestParam(value = "mq_port") String mq_port,
            @RequestParam(value = "mq_message") String mq_message,
            @RequestParam(value = "mq_pid") String mq_pid,
            @RequestParam(value = "template") String template,
            HttpServletRequest request

    ) {
        try {
            mq_message = mq_message.replaceAll("\r|\n", "").replaceAll("(?<=>)\\s+(?=<)", "");
            MQ mq = new MQ(mq_hostname, mq_qmgname, mq_channel, Integer.valueOf(mq_ccsid), Integer.valueOf(mq_port));
            mq.setMqUser(leftTreeService.getMqUser(mq_pid, String.format("%s,%s,%s,%s,%s", mq_hostname, mq_qmgname, mq_channel, mq_ccsid, mq_port)));
            util.set_mq(mq);
            String userIP = getIp(request);//IP
            String useType = String.valueOf(UseType.MessageAdd.getValue());//type
            template = com.gome.test.utils.StringUtils.isEmpty(template)?template:String.format("%s_%s.xml",template,mq_pid);
            MQLogInfo mqLogInfo = new MQLogInfo(mq_hostname, mq_port, mq_channel, mq_qmgname, mq_qname, mq.getMqUser().get_user(), userIP, useType,template);
            mqLogInfo.setMq_message(mq_message);

            Result result = util.sendMessage(mqLogInfo);
            return result;

        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }


    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }


    @RequestMapping(value = "/use")
    @ResponseBody
    Result use(
            @RequestParam(value = "mq_qname") String mq_qname,
            @RequestParam(value = "mq_hostname") String mq_hostname,
            @RequestParam(value = "mq_qmgname") String mq_qmgname,
            @RequestParam(value = "mq_channel") String mq_channel,
            @RequestParam(value = "mq_ccsid") String mq_ccsid,
            @RequestParam(value = "mq_port") String mq_port,
            @RequestParam(value = "mq_pid") String mq_pid,
            HttpServletRequest request

    ) {
        try {
            MQ mq = new MQ(mq_hostname, mq_qmgname, mq_channel, Integer.valueOf(mq_ccsid), Integer.valueOf(mq_port));
            mq.setMqUser(leftTreeService.getMqUser(mq_pid, String.format("%s,%s,%s,%s,%s", mq_hostname, mq_qmgname, mq_channel, mq_ccsid, mq_port)));
            util.set_mq(mq);
            String userIP = getIp(request);//IP
            String useType = String.valueOf(UseType.MessageUse.getValue());//type
            MQLogInfo mqLogInfo = new MQLogInfo(mq_hostname, mq_port, mq_channel, mq_qmgname, mq_qname, mq.getMqUser().get_user(), userIP, useType,"");


            Result result = util.useMessage(mqLogInfo);
            return result;

        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }

    //
    @RequestMapping(value = "/xmlformat")
    @ResponseBody
    Result xmlformat(
            @RequestParam(value = "mess") String mess


    ) {
        try {
            Result result = new Result(XmlFormatUtil.format(mess));
            return result;

        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }

    @RequestMapping(value = "/tmp")
    ModelAndView toTmpPage() {
        return new ModelAndView("tmp");
    }


    @RequestMapping(value = "/tmp/data")
    @ResponseBody
    Result getTmpData(
            @RequestParam(value = "tmpName") String tmpName,
            @RequestParam(value = "pid") String pid,
            @RequestParam(value = "logId") String logId) {
        try {

            Result result = new Result(tmpUtil.getMQTemplate(tmpName, pid,logId));
            return result;
        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }


    private List<Template> testTmp(String tmpName) {
        List<Template> tmpList = new ArrayList<>();

        Template template = new Template();
        template.setName(tmpName);
        template.setPath("track_n_trace/transaction_id");
        template.setIsempty(false);
        template.setDtype("String");
        template.setDec("Test tmp test");
        template.setIsmust(false);
        template.setRemark("Not mustbe run");
        template.setValue("Tmp value value value");
        tmpList.add(template);

        Template template1 = new Template();
        template1.setName(tmpName);
        template1.setPath("track_n_trace/transaction_id");
        template1.setIsempty(false);
        template1.setDtype("String");
        template1.setDec("Test tmp test");
        template1.setIsmust(false);
        template1.setRemark("Not mustbe run");
        template1.setValue("Tmp value value value");
        tmpList.add(template1);

        return tmpList;
    }

    @RequestMapping(value = "/tmp/{title}/{pid}/{hostName}/{qmgName}/{channel}/{ccsid}/{port}/{qName}/{template}/{qType}/{qisCmd}/tmp")
    public ModelAndView tmpView(
            @PathVariable(value = "hostName") String hostName,
            @PathVariable(value = "qmgName") String qmgName,
            @PathVariable(value = "qName") String qName,
            @PathVariable(value = "channel") String channel,
            @PathVariable(value = "ccsid") String ccsid,
            @PathVariable(value = "port") String port,
            @PathVariable(value = "pid") String pid,
            @PathVariable(value = "qType") String qType,
            @PathVariable(value = "qisCmd") String qisCmd,
            @PathVariable(value = "title") String title,
            @PathVariable(value = "template") String template) {

        ModelAndView mv = new ModelAndView("tmp");
        mv.addObject("mq_pid", pid);
        mv.addObject("mq_hostname", hostName);
        mv.addObject("mq_qmgname", qmgName);
        mv.addObject("mq_channel", channel);
        mv.addObject("mq_ccsid", ccsid);
        mv.addObject("mq_port", port);
        mv.addObject("mq_qname", qName);
        mv.addObject("template",template);
        mv.addObject("mq_type", qType);
        mv.addObject("mq_iscmd", qisCmd);
        mv.addObject("title", title);

        return mv;
    }
    @Autowired
    LogUtil logUtil;

    @RequestMapping(value = "/tmp/genmsg",method = RequestMethod.POST)
    @ResponseBody
    public Result genMsg(
            @RequestParam(value = "pathList[]") ArrayList<String> pathList,
            @RequestParam(value = "typeList[]") ArrayList<String> typeList,
            @RequestParam(value = "valueList[]") ArrayList<String> valueList
    ) {
        try {
            String xmlString = tmpUtil.convertToXmlString(pathList, typeList, valueList);
            return new Result(xmlString);
        } catch (Exception ex) {
            return new Result(true, ex.getMessage());
        }

    }


    @RequestMapping(value = "/tmp/logdata")
    @ResponseBody
    Result getTmpLogData(
            @RequestParam(value = "tmpName") String tmpName,
            @RequestParam(value = "pid") String pid) {
        try {
            String template=String.format("%s_%s.xml",tmpName,pid);
            Result result = new Result(logUtil.getLogListByTemplate(template));

            return result;
        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }

}
