package com.gome.test.mq.controller;

import com.gome.test.mq.bo.LogUtil;
import com.gome.test.mq.bo.TemplateUtil;
import com.gome.test.mq.bo.XmlFormatUtil;
import com.gome.test.mq.model.MQLogInfo;
import com.gome.test.mq.model.Result;
import com.gome.test.mq.model.UseType;
import com.gome.test.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangjiadi on 15/12/25.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/log")
public class LogController {

    @Autowired
    LogUtil logUtil;

    @Autowired
    TemplateUtil templateUtil;
//10.126.53.32,QM_MOCK02,C_SVR_GOME02,2426
    @RequestMapping(value = "/getlogs/{templatepart}/{host}/{qmgName}/{channel}/{port}/log")
    public ModelAndView logModifyView(
            @PathVariable(value = "templatepart") String templatepart
            , @PathVariable(value = "host") String host
            , @PathVariable(value = "qmgName") String qmgName
            , @PathVariable(value = "channel") String channel
            , @PathVariable(value = "port") String port
    ) {
        ModelAndView mv = new ModelAndView("logs");
        List<String> template= templateUtil.getAllTemplate();
        template.add(0,"");
        mv.addObject("templatemap", template);
        mv.addObject("templatepart",templatepart);
        mv.addObject("qmgName",qmgName);
        mv.addObject("channel",channel);
        mv.addObject("host",host);
        mv.addObject("port",port);
        return mv;
    }

    @RequestMapping(value = "/getTemplates" , method =  RequestMethod.GET)
    @ResponseBody
    Result getTemplates() {
        try {
            List<String> template= templateUtil.getAllTemplate();
            template.add(0,"");
            Result result = new Result(template);

            return result;
        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }
//    /log/searchAll?begin=&end=&template=&host=10.126.53.124&qmgName=QM_TEST3_GOME02&channel=C_SVR_GOME02&port=1416"
    @RequestMapping(value = "/searchAll" , method =  RequestMethod.GET)
    @ResponseBody
    Result getTmpLogData( @RequestParam(value = "begin") String begin,
                          @RequestParam(value = "end") String end,
                          @RequestParam(value = "template") String template,
                          @RequestParam(value = "host") String host,
//
                          @RequestParam(value = "qmgName") String qmgName,
                          @RequestParam(value = "channel") String channel,
                          @RequestParam(value = "port") String port
    ) {
        try {
            if(!StringUtils.isEmpty(begin)) {
                if (StringUtils.isEmpty(end)) {
                    end = begin;

                }

                end = getNextDate(end);
            }

            List<MQLogInfo> list = logUtil.getLogList(begin,end,template,host,qmgName,channel,port);

            for(MQLogInfo info : list)
            {
                conventLogInfo(info);
            }

            Result result = new Result(list);

            return result;
        } catch (Exception ex) {
            return new Result(true, ex.toString());
        }
    }

    private void  conventLogInfo(MQLogInfo info)
    {
        info.setMq_useType(UseType.valueOf(Integer.valueOf(info.getMq_useType())).getName());
    }


    //得到当前日期的后一个日期
    private String getNextDate(String dateStr)
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(dateStr);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, 1);//把日期往后增加一天
            date = calendar.getTime();
            return sdf.format(date);
        }catch (Exception ex)
        {
            return  null;
        }

    }

}

