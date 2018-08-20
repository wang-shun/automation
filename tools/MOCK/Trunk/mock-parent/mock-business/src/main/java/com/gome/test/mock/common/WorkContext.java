package com.gome.test.mock.common;

import java.util.Map;

import com.gome.test.mock.model.bean.Template;
import com.gome.test.mock.pipes.http.SimpleHttpRequest;
import com.gome.test.mock.pipes.http.SimpleHttpResponse;

public class WorkContext implements java.io.Serializable {

    private static final long serialVersionUID = 8804294293746136542L;

    private static WorkContext workContext;

    private static Template template;

    private static boolean isWebServcie = false;

    private static String soapProtocol;

    private WorkContext() {

    }

    public static synchronized WorkContext getContextInstance() {
        if (workContext == null) {
            workContext = new WorkContext();
        }
        return workContext;
    }

    private String sessionId;
    private SimpleHttpRequest request;
    private SimpleHttpResponse response;
    private Map<String, Object> resMap;
    private Map<String, String> paramMap;

    public SimpleHttpRequest getRequest() {
        return this.request;
    }

    public void setRequest(SimpleHttpRequest request) {
        this.request = request;
    }

    public SimpleHttpResponse getResponse() {
        return this.response;
    }

    public void setResponse(SimpleHttpResponse response) {
        this.response = response;
    }

    public Map<String, Object> getResMap() {
        return this.resMap;
    }

    public void setResMap(Map<String, Object> resMap) {
        this.resMap = resMap;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, String> getParamMap() {
        return this.paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public static boolean isWebServcie() {
        return isWebServcie;
    }

    public static void setWebServcie(boolean isWebServcie) {
        WorkContext.isWebServcie = isWebServcie;
    }

    public static String getSoapProtocol() {
        return soapProtocol;
    }

    public static void setSoapProtocol(String soapProtocol) {
        WorkContext.soapProtocol = soapProtocol;
    }

    public static Template getTemplate() {
        return template;
    }

    public static void setTemplate(Template template) {
        WorkContext.template = template;
    }

}
