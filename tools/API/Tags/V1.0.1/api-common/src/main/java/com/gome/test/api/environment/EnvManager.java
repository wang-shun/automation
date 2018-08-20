package com.gome.test.api.environment;

import com.gome.test.api.annotation.SetUp;
import com.gome.test.api.testng.BaseConfig;
import com.gome.test.api.testng.HttpBaseConfig;
import com.gome.test.api.utils.DBUtils;
import com.gome.test.api.annotation.TearDown;
import com.gome.test.api.client.IMessageClient;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvManager {

    protected IMessageClient messageClient;

    public void setMessageClient(IMessageClient messageClient) {
        this.messageClient = messageClient;
    }

    //    public void setMessageClient() {
//        this.messageClient = new HttpMessageClient();
//    }
//    
    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为[value]")
    public void set(String name, String value) {
        BaseConfig.getContext().put(name, value);
    }

    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为[value]")
    public void addContext(String name, String value) {
        BaseConfig.getContext().put(name, value);
    }

    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为执行[sqlId]对应的SQL语句返回值")
    public void addContextFromSQL(String name, String sqlId) throws Exception {
        List<String[]> results = DBUtils.querySQL(sqlId);
        addContext(name, results.get(0)[0]);
    }

    @SetUp(description = "将[sqlId]对应的SQL语句返回值放到上下文环境中")
    public void addContextsFromSQL(String sqlId) throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        List<String[]> results = DBUtils.querySQL(sqlId, meta);
        for (Integer k : meta.keySet()) {
            String name = meta.get(k);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < results.size(); ++i) {
                if (0 != i) {
                    sb.append(",");
                }
                sb.append(results.get(i)[k]);
            }
            addContext(name, sb.toString());
        }
    }

    @SetUp(description = "执行[sqlId]对应的SQL语句")
    public void executeSQL(String sqlId) throws Exception {
        DBUtils.updateDB(sqlId);
    }

    @SetUp(description = "移除名为[name]的上下文环境")
    public void removeContext(String name) {
        BaseConfig.getContext().remove(name);
    }

    @SetUp(description = "清空上下文环境")
    public void clearContext() {
        BaseConfig.getContext().clear();
    }

    @TearDown(description = "将json返回值中[jsonPath]对应的字段，在后续测试步骤中可以以[name]引用")
    public void dataTransfer(String jsonPath, String name) {
        String response = messageClient.getResponse();
        String value = JsonPath.read(response, jsonPath).toString();
        BaseConfig.addToContext(name, value);
    }
}
