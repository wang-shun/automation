package com.gome.test.api.ide.model;

import com.gome.test.api.model.Entities;
import com.gome.test.api.model.UrlParams;
import com.gome.test.api.model.Headers;

public class HttpRequest {

    private String httpUrl;
    private String httpMethod;
    private Headers headers;
    private UrlParams urlParams;
    private Entities entities;

    public HttpRequest() {
    }

    public HttpRequest(String httpUrl, String httpMethod,
                       Headers headers, UrlParams urlParams, Entities entities) {
        this.httpUrl = httpUrl;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.urlParams = urlParams;
        this.entities = entities;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public UrlParams getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(UrlParams urlParams) {
        this.urlParams = urlParams;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntitites(Entities entities) {
        this.entities = entities;
    }
}
