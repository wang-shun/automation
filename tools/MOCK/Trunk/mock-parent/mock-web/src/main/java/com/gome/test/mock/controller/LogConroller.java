package com.gome.test.mock.controller;

import com.gome.test.mock.dao.ApiDao;
import com.gome.test.mock.dao.RequestResponseLogDao;
import com.gome.test.mock.model.Result;
import com.gome.test.mock.model.TableResult;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.RequestResponseLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by zhangjiadi on 15/10/17.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/log")//log/search
public class LogConroller {

    @Autowired
    private RequestResponseLogDao requestResponseLogDao;

    @Autowired
    private ApiDao apiDao;

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public Result getAllRequestResponseLogDao(
    ) {
        try {
            return new Result(requestResponseLogDao.getAll());

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }


    @RequestMapping(value = "/searchAll")
    @ResponseBody
    TableResult getAllLogs() {
        try {
            List logLists = requestResponseLogDao.queryAllListBySearch();
            if (logLists != null) {
                return new TableResult(false, 1, logLists.size(), logLists.size(), logLists.toArray());
            } else {
                return new TableResult();
            }
        } catch (Exception ex) {
            return new TableResult(true, ex.toString());
        }
    }


    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result logsDetail (
            @RequestParam(value = "log_id", required = true) int log_id
    ) {
        try {
            RequestResponseLog log = requestResponseLogDao.get(log_id);
            Api api=new Api();
            if(log!=null)
            {
                api=apiDao.get(log.getApiId());
            }
            Object[] result= {log,api} ;

            return new Result(result);

        }catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }

    }

    @RequestMapping(value = "/search")
    public ModelAndView getHostPage() {
        return new ModelAndView("mocklog");
    }
}
