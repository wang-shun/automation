package com.gome.test.order.process.mail;


import com.gome.test.order.process.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Service
public class EmailService {

    public EmailInfo getEmailInfo() {
        return emailInfo;
    }

    public void setEmailInfo(EmailInfo emailInfo) {
        this.emailInfo = emailInfo;
    }

    @Autowired
    private EmailInfo emailInfo;


    final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String htmlfileContent, List<String> mailToList, List<String> ccList) {
        //装载邮件MimeMessage
        try {

            Properties props = new Properties();
            EmailAutherticator auth = new EmailAutherticator(emailInfo.getUsername(), emailInfo.getPassword());
            props.put("mail.smtp.host", emailInfo.getServerHost());
            /**
             * 自动后成发送人邮件!!
             */
            props.put("mail.smtp.starttls.enable", "true");   //gmail发时:
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, auth);
            /**
             * 设置session,和邮件服务器进行通讯
             */
            MimeMessage message = new MimeMessage(session);
            Multipart mp = new MimeMultipart("related");// related意味着可以发送html格式的邮件
            BodyPart bodyPart = new MimeBodyPart();// 正文
            bodyPart.setDataHandler(new DataHandler(htmlfileContent, "text/html;charset=UTF-8"));// 网页格式

            mp.addBodyPart(bodyPart);
            message.setContent(mp);//
            message.setSubject(emailInfo.getSubject());//设置邮件主题
            message.setHeader(emailInfo.getMail_head_info(), emailInfo.getMail_head_value());//设置邮件标题
            message.setSentDate(new Date());//设置邮件发送时期
            Address address = new InternetAddress(emailInfo.getMail_from(), emailInfo.getPersonalName());//发件人地址 发件人名称
            message.setFrom(address);//设置邮件发送者的地址
            if (!mailToList.isEmpty()) {
                for (String mailTo : mailToList) {
                    Address toaddress = new InternetAddress(mailTo); //设置邮件接收者的地址
                    message.addRecipient(Message.RecipientType.TO, toaddress);
                }
            }
            if (!ccList.isEmpty()) {
                for (String cc : ccList) {
                    Address toaddress = new InternetAddress(cc); //设置邮件抄送者的地址
                    message.addRecipient(Message.RecipientType.CC, toaddress);
                }
            }
            //发送邮件
            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendReport(List<Order> listHistory) {
        //邮件内容
        String emailContent = getEmailContent(listHistory);
        //邮件服务相关公共配置

        emailInfo.setSubject(emailInfo.getSubject());

        emailInfo.setMail_head_info(emailInfo.getMail_head_info());
        emailInfo.setMail_head_value(emailInfo.getMail_head_value());

        //收件人和抄送人
        List<String> toList = new ArrayList<String>();
        List<String> ccList = new ArrayList<String>();


//        emailInfo = configEmailInfo(emailInfo);
        if (true) {
            for (String to : emailInfo.getToEmail()) {
                toList.add(to);
            }
            for (String cc : emailInfo.getCCEmail()) {
                ccList.add(cc);
            }
            sendEmail(emailContent, toList, ccList);
        } else {
            toList.add("weijianxing@yolo24.com");
            emailInfo.setSubject("群组收件人有误-" + emailInfo.getSubject());
            sendEmail(emailContent, toList, ccList);
        }
    }

    public void sendReport(List<Order> listHistory, String sendTo) {
        //邮件内容
        String emailContent = getEmailContent(listHistory);
        //邮件服务相关公共配置

        emailInfo.setSubject(emailInfo.getSubject());

        emailInfo.setMail_head_info(emailInfo.getMail_head_info());
        emailInfo.setMail_head_value(emailInfo.getMail_head_value());

        //收件人和抄送人
        List<String> toList = new ArrayList<String>();
        List<String> ccList = new ArrayList<String>();


//        emailInfo = configEmailInfo(emailInfo);
        if (true) {
            //add target user to send list.
            if (sendTo != null) {

                if (sendTo.contains(",")) {
                    String[] send = sendTo.split(",");
                    for (int i = 0; i < send.length; i++) {
                        toList.add(send[i]);
                    }
                } else {
                    toList.add(sendTo);
                }
            }
//            for(String to : emailInfo.getToEmail()){
//                toList.add(to);
//            }

            for (String cc : emailInfo.getCCEmail()) {
                ccList.add(cc);
            }
            sendEmail(emailContent, toList, ccList);
        } else {
            toList.add("weijianxing@yolo24.com");
            emailInfo.setSubject("群组收件人有误-" + emailInfo.getSubject());
            sendEmail(emailContent, toList, ccList);
        }
    }

    public String getEmailContent(List<Order> listHistory) {

        StringBuilder personalList = new StringBuilder();
        personalList.append("<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +

                "    <title>${HEAD_INFO}</title>\n" +

                "    <title>订单流转结果</title>\n" +

                "    <style type=\"text/css\">\n" +
                "        body {\n" +
                "            font-size: .75em;\n" +
                "            font-family: Verdana, Helvetica, Sans-Serif;\n" +
                "            margin: 10px;\n" +
                "            padding: 10px;\n" +
                "            color: #696969;\n" +
                "        }\n" +
                "\n" +
                "        .page {\n" +
                "            width: 90%;\n" +
                "            margin-left: auto;\n" +
                "            margin-right: auto;\n" +
                "        }\n" +
                "\n" +
                "        #header {\n" +
                "            position: relative;\n" +
                "            margin-bottom: 0px;\n" +
                "            color: #000;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        div#title {\n" +
                "            display: block;\n" +
                "            float: left;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "\n" +
                "        #main {\n" +
                "            padding: 30px 30px 15px 30px;\n" +
                "            background-color: #FFFFFF;\n" +
                "            margin-bottom: 30px;\n" +
                "            _height: 1px; /* only IE6 applies CSS properties starting with an underscore */\n" +
                "        }\n" +
                "\n" +
                "        h1, h2, h3, h4, h5, h6 {\n" +
                "            font-size: 1.5em;\n" +
                "            color: #000;\n" +
                "            font-family: Arial, Helvetica, sans-serif;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            font-size: 2em;\n" +
                "            padding-bottom: 0;\n" +
                "            margin-bottom: 0;\n" +
                "        }\n" +
                "\n" +
                "        h2 {\n" +
                "            padding: 0 0 10px 0;\n" +
                "        }\n" +
                "\n" +
                "        h3 {\n" +
                "            font-size: 1.2em;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            background-color: #FFFFFF;\n" +
                "            border-right: solid 1px #e8eef4;\n" +
                "            border-bottom: solid 1px #e8eef4;\n" +
                "            width: 98%;\n" +
                "        }\n" +
                "\n" +
                "        table td {\n" +
                "            padding: 5px;\n" +
                "            border-left: solid 1px #e8eef4;\n" +
                "            border-top: solid 1px #e8eef4;\n" +
                "        }\n" +
                "\n" +
                "        table th {\n" +
                "            padding: 6px 5px;\n" +
                "            text-align: left;\n" +
                "            background-color: #e8eef4;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"page\">\n" +
                " \t<h2>FYI:</h2>\n" +
                "    <h3>已经流转完成的订单信息</h3>\n" +
                "    <hr><br>\n" +
                "    <table>\n" +
                "        <tr>\n" +

                "            <td colspan=\"4\">\n" +
                "                <h3>订单信息</h3>\n" +

/*                "            <td colspan=\"9\">\n" +
                "                <h3>配送单号处理结果</h3>\n" +
                "            </td>\n" +*/
                "        </tr>\n" +
                "        <tr>\n" +
                "            <th>\n" +

                "                订单号\n" +
                "            </th>\n" +
                "            <th>\n" +
                "                配送单号\n" +
                "            </th>\n" +
                "            <th>\n" +

                "                配送单状态\n" +
                "            </th>\n" +

                "            <th>\n" +
                "                执行描述\n" +
                "            </th>\n" +
                "        </tr>\n");
        for (Order info : listHistory) {

            personalList.append(" <tr>\n"
                            +
                            "            <td>\n"
//                            +
//                    "                <b>"
            );


            personalList.append(info.getOrderNo());


            personalList.append(" </b>\n" +
                            "            <td>\n"
//                    +
//                    "                <b>"
            );
            personalList.append(info.getOrderNumber());


            personalList.append(" </b>\n" +
                            "            <td>\n"
//                    +
//                    "                <b>"
            );
            personalList.append(info.getEndStatus());


            personalList.append(" </b>\n" +
                    "            <td>\n"
            );
            personalList.append(info.getDesc());

/*            personalList.append(" </b>\n" +
                    "            <td>\n" +
                    "        </tr>");*/
            personalList.append(" </tr>");
        }

        return personalList.toString();
    }


    private boolean realDiff(String skuSearch, int itemSearch) {
        int iSkuS = skuSearch.equals("有货") ? 1 : 0;

        return iSkuS != itemSearch;
    }

    private String htmlStyle(Order info) {
        String styleStr = "";
        styleStr = "<b style=\"color:#FF0000\">\n";
        boolean deepColor = false;
        if (deepColor) {

        } else {
            styleStr = "<b>\n";
        }
        return styleStr;

    }


}
