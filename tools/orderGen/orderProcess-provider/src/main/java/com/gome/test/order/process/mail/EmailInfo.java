package com.gome.test.order.process.mail;

/**
 * Created by lizonglin on 2015/9/2/0002.
 */
public class EmailInfo {
    private String username;
    private String password;
    private String serverHost;
    private String subject;
    private String mail_head_info;
    private String mail_head_value;
    private String mail_from;
    private String personalName;
    private String toEmail[];
    private String CCEmail[];


    private String pricediff_mail_head_info;
    private String pricediff_mail_head_value;
    private String pricediff_toEmail[];
    private String pricediff_CCEmail[];
    private String pricediff_subject;


    private String beanstalk_mail_head_info;
    private String beanstalk_mail_head_value;
    private String beanstalk_toEmail[];

    public String getBeanstalk_mail_head_info() {
        return beanstalk_mail_head_info;
    }

    public void setBeanstalk_mail_head_info(String beanstalk_mail_head_info) {
        this.beanstalk_mail_head_info = beanstalk_mail_head_info;
    }

    public String getBeanstalk_mail_head_value() {
        return beanstalk_mail_head_value;
    }

    public void setBeanstalk_mail_head_value(String beanstalk_mail_head_value) {
        this.beanstalk_mail_head_value = beanstalk_mail_head_value;
    }

    public String[] getBeanstalk_toEmail() {
        return beanstalk_toEmail;
    }

    public void setBeanstalk_toEmail(String[] beanstalk_toEmail) {
        this.beanstalk_toEmail = beanstalk_toEmail;
    }

    public String[] getBeanstalk_CCEmail() {
        return beanstalk_CCEmail;
    }

    public void setBeanstalk_CCEmail(String[] beanstalk_CCEmail) {
        this.beanstalk_CCEmail = beanstalk_CCEmail;
    }

    public String getBeanstalk_subject() {
        return beanstalk_subject;
    }

    public void setBeanstalk_subject(String beanstalk_subject) {
        this.beanstalk_subject = beanstalk_subject;
    }

    private String beanstalk_CCEmail[];
    private String beanstalk_subject;


    public String getPricediff_mail_head_info() {
        return pricediff_mail_head_info;
    }

    public void setPricediff_mail_head_info(String pricediff_mail_head_info) {
        this.pricediff_mail_head_info = pricediff_mail_head_info;
    }

    public String getPricediff_mail_head_value() {
        return pricediff_mail_head_value;
    }

    public void setPricediff_mail_head_value(String pricediff_mail_head_value) {
        this.pricediff_mail_head_value = pricediff_mail_head_value;
    }

    public String[] getPricediff_toEmail() {
        return pricediff_toEmail;
    }

    public void setPricediff_toEmail(String[] pricediff_toEmail) {
        this.pricediff_toEmail = pricediff_toEmail;
    }

    public String[] getPricediff_CCEmail() {
        return pricediff_CCEmail;
    }

    public void setPricediff_CCEmail(String[] pricediff_CCEmail) {
        this.pricediff_CCEmail = pricediff_CCEmail;
    }

    public String getPricediff_subject() {
        return pricediff_subject;
    }

    public void setPricediff_subject(String pricediff_subject) {
        this.pricediff_subject = pricediff_subject;
    }


    public String[] getToEmail() {
        return toEmail;
    }

    public void setToEmail(String[] toEmail) {
        this.toEmail = toEmail;
    }

    public String[] getCCEmail() {
        return CCEmail;
    }

    public void setCCEmail(String[] CCEmail) {
        this.CCEmail = CCEmail;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMail_head_info() {
        return mail_head_info;
    }

    public void setMail_head_info(String mail_head_info) {
        this.mail_head_info = mail_head_info;
    }

    public String getMail_head_value() {
        return mail_head_value;
    }

    public void setMail_head_value(String mail_head_value) {
        this.mail_head_value = mail_head_value;
    }

    public String getMail_from() {
        return mail_from;
    }

    public void setMail_from(String mail_form) {
        this.mail_from = mail_form;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }


}
