package com.gome.test.api.ide.model;

import com.gome.test.api.model.Entities;
import com.gome.test.api.model.Headers;
import com.gome.test.api.model.Steps;
import com.gome.test.api.model.UrlParams;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

public class TestCase {

    private String id;
    private String name;
    private HttpRequest httpRequest;
    private String verifyClass;
    private Steps verifySteps;
    private String setUpClass;
    private Steps setUpSteps;
    private String tearDownClass;
    private Steps tearDownSteps;
    private String owner;
    private CaseVariables caseVariables;

    public TestCase() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setVerifyClass(String verifyClass) {
        this.verifyClass = verifyClass;
    }

    public String getVerifyClass() {
        return verifyClass;
    }

    public void setVerifySteps(Steps verifySteps) {
        this.verifySteps = verifySteps;
    }

    public Steps getVerifySteps() {
        return verifySteps;
    }

    public void setSetUpClass(String setUpClass) {
        this.setUpClass = setUpClass;
    }

    public String getSetUpClass() {
        return setUpClass;
    }

    public void setSetUpSteps(Steps setUpSteps) {
        this.setUpSteps = setUpSteps;
    }

    public Steps getSetUpSteps() {
        return setUpSteps;
    }

    public void setTearDownClass(String tearDownClass) {
        this.tearDownClass = tearDownClass;
    }

    public String getTearDownClass() {
        return tearDownClass;
    }

    public void setTearDownSteps(Steps tearDownSteps) {
        this.tearDownSteps = tearDownSteps;
    }

    public Steps getTearDownSteps() {
        return tearDownSteps;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public CaseVariables getCaseVariables() {
        return caseVariables;
    }

    public void setCaseVariables(CaseVariables caseVariables) {
        this.caseVariables = caseVariables;
    }

    @JsonIgnore
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        if (null != httpRequest) {
            params.put("httpUrl", httpRequest.getHttpUrl());
            params.put("httpMethod", httpRequest.getHttpMethod());
            Headers headers = httpRequest.getHeaders();
            if (null != headers) {
                params.put("headers", headers.toJsonString());
            }
            UrlParams urlParams = httpRequest.getUrlParams();
            if (null != urlParams) {
                params.put("urlParams", urlParams.toJsonString());
            }
            Entities entities = httpRequest.getEntities();
            if (null != entities) {
                params.put("entities", entities.toJsonString());
            }
        }
        params.put("verifyClass", verifyClass);
        if (null != verifySteps) {
            params.put("verifySteps", verifySteps.toJsonString());
        }
        params.put("setUpClass", setUpClass);
        if (null != setUpSteps) {
            params.put("setUpSteps", setUpSteps.toJsonString());
        }
        params.put("tearDownClass", tearDownClass);
        if (null != tearDownSteps) {
            params.put("tearDownSteps", tearDownSteps.toJsonString());
        }
        if (null != caseVariables) {
            params.put("caseVariables", caseVariables.toJsonString());
        }
        return params;
    }

    public static TestCase loadFrom(String id, String name,
                                    Map<String, String> params, String owner) {
        TestCase testCase = new TestCase();
        testCase.setId(id);
        testCase.setName(name);
        testCase.setOwner(owner);
        String httpUrl = params.get("httpUrl");
        String httpMethod = params.get("httpMethod");
        Headers headers = Headers.loadFrom(params.get("headers"));
        UrlParams urlParams = UrlParams.loadFrom(params.get("urlParams"));
        Entities entities = Entities.loadFrom(params.get("entities"));
        HttpRequest httpRequest = new HttpRequest(httpUrl, httpMethod,
                headers, urlParams, entities);
        testCase.setHttpRequest(httpRequest);
        String verifyClass = params.get("verifyClass");
        testCase.setVerifyClass(verifyClass);
        Steps verifySteps = Steps.loadFrom(params.get("verifySteps"));
        testCase.setVerifySteps(verifySteps);
        String setUpClass = params.get("setUpClass");
        testCase.setSetUpClass(setUpClass);
        Steps setUpSteps = Steps.loadFrom(params.get("setUpSteps"));
        testCase.setSetUpSteps(setUpSteps);
        String tearDownClass = params.get("tearDownClass");
        testCase.setTearDownClass(tearDownClass);
        Steps tearDownSteps = Steps.loadFrom(params.get("tearDownSteps"));
        testCase.setTearDownSteps(tearDownSteps);
        CaseVariables caseVariables = CaseVariables.loadFrom(params.get("caseVariables"));
        testCase.setCaseVariables(caseVariables);
        return testCase;
    }
}
