package com.gome.test.order.process.schedule;

import com.gome.test.order.process.Order;
import com.gome.test.order.process.OrderProcessProvider;
import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * send processed order list.
 */
public class MailJob {
    @Autowired
    EmailService emailService;
    final Logger logger = LoggerFactory.getLogger(MailJob.class);


    public void init() {
        ApplicationContext context = OrderProcessProvider.getContext();
        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:mailInfo.xml");
        if (context != null) {
            emailService = context.getBean(EmailService.class);
        }
    }

    public synchronized void sendMail() {

        init();
        List<Order> reportList = ProcessedOrder.getInstance().getProcessedOrders();
        if (reportList != null && reportList.size() > 0) {
            logger.info("===============get processed order size is : " + reportList.size());

            Map<String, List<Order>> maillist = filterMail(reportList);

            for (Map.Entry<String, List<Order>> entry : maillist.entrySet()) {
                emailService.sendReport(entry.getValue(), entry.getKey());
            }
//            emailService.sendReport(reportList);
            logger.info("Processed orders have been sent.");
            reportList.clear();
            ProcessedOrder.getInstance().getProcessedOrders().clear();
        } else {
            logger.info("get Processed orders  data is null.");
        }

    }


    private Map<String, List<Order>> filterMail(List<Order> orders) {
        Map<String, List<Order>> infos = new HashMap<String, List<Order>>();
        for (Order order : orders) {
            if (infos.size() == 0) {
                List<Order> first = new ArrayList<Order>();
                first.add(order);
                infos.put(order.getEmailCount(), first);
            } else {
                List<Order> olist = infos.get(order.getEmailCount());
                if (olist == null) {
                    List<Order> first = new ArrayList<Order>();
                    first.add(order);
                    infos.put(order.getEmailCount(), first);
                } else {
                    olist.add(order);
                }
            }
        }
        return infos;
    }



/*

    @Test
    public void testSendMail(){

        Order initOrder = new Order();
        initOrder.setOrderNo("123");
        initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
        initOrder.setProductType("G3pp");
        initOrder.setExpectStatus("DL");
        initOrder.setCurrentStatus("PR");
        initOrder.setEmailCount("liangwei-ds@gomeplus.com,test@gomeplus.com");
        ProcessedOrder.getInstance().getProcessedOrders().add(initOrder);

        Order initOrder1 = new Order();
        initOrder1.setOrderNo("110");
        initOrder1.setStartTime(OrderUtils.getCurrentTimestamp());
        initOrder1.setProductType("G3pp");
        initOrder1.setExpectStatus("DL");
        initOrder1.setCurrentStatus("PR");
        initOrder1.setEmailCount("liangwei-ds@gomeplus.com,test@gomeplus.com");
        ProcessedOrder.getInstance().addFinalOrder(initOrder1);

        sendMail();
    }

*/


}
