package com.gome.test.mock.db.service;

import java.util.List;

import com.gome.test.mock.domain.bean.Api;
import com.gome.test.mock.exception.MyException;

/**
 * Created by chaizhongbao on 2015/9/25.
 */
public interface ApiService {
    /**
     * 增加Api
     * @param model
     */
    public void addApi(Api model) throws MyException;

    /**
     * 查询Api列表
     */
    public List<Api> queryApis() throws MyException;

    /**
     * 查询Api列表
     */
    public List<Api> queryApiList(String url, int port) throws MyException;

    /**
     * 查询Api列表
     */
    public List<Api> queryApiList(String domain, String url, int port) throws MyException;

}
