package com.gome.test.mock.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gome.test.mock.cnst.ConstDefine;
import com.gome.test.mock.dao.base.BaseDao;
import com.gome.test.mock.exception.MyException;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.vo.HostVo;

/**
 * Created by Administrator on 2015/10/14.
 */

@Repository
@Transactional
public class ApiDao extends BaseDao<Api> {
    @Autowired
    private HostDao hostDao;

    public List<Api> getApiList() {
        String sql = "select ID,HOST_ID,API_NAME,KEY_WORDS,INTERCEPT_PARAM,DESCRIPT,ENABLE,TEMPLATE_ID from m_api ";
        List apis = this.sqlQuery(sql);
        return apis == null ? new ArrayList<Api>() : apis;
    }

    @SuppressWarnings("unchecked")
    public List<Api> queryApis() throws MyException {
        String hql = "from Api where enable = " + ConstDefine.INT_YES;
        return this.find(hql);
    }

    @SuppressWarnings("unchecked")
    public List<Api> queryApiList(List<Integer> hostIds) throws MyException {
        String hql = "from Api a where a.enable = " + ConstDefine.INT_YES + " and a.hostId in ( @param1 ) ";
        String hostIdStr = "";
        for (Integer hostId : hostIds) {
            if (hostId != null && hostId > 0) {
                hostIdStr += hostId + ",";
            }
        }
        hostIdStr = (hostIdStr.length() > 0) ? hostIdStr.substring(0, hostIdStr.length() - 1) : hostIdStr;
        return this.find(hql.replace("@param1", hostIdStr));
    }

    public Api queryApiById(int apiId) throws MyException {
        return this.get(apiId);
    }

    public List<Api> queryApiList(String domain, String url, int port) throws MyException {
        List<HostVo> hostVos = this.hostDao.queryHostVosByHostUrlPort(domain, url, port);
        List<Integer> hostIds = new ArrayList<Integer>();
        if (hostVos != null && hostVos.size() > 0) {
            for (HostVo hostVo : hostVos) {
                if (hostVo != null) {
                    if (!hostIds.contains(hostVo.getHost().getId())) {
                        hostIds.add(hostVo.getHost().getId());
                    }
                }
            }
            return this.queryApiList(hostIds);
        } else {
            return null;
        }

    }

    public List queryApiListByApiName(String apiName) {
        String sql = String.format("select ID,HOST_ID,TEMPLATE_ID,API_NAME,KEY_WORDS,INTERCEPT_PARAM,DESCRIPT,ENABLE from m_api where API_NAME = '%s'", apiName);
        List apis = this.sqlQuery(sql);
        return apis == null ? new ArrayList<Api>() : apis;
    }

    public List queryApiListBySearch() {

        String sql = "select api.ID,api.HOST_ID,api.TEMPLATE_ID,api.API_NAME,api.KEY_WORDS,api.INTERCEPT_PARAM,api.DESCRIPT,api.ENABLE,host.DOMAIN,host.SERVICE_NAME,host.URL from m_api api, m_host host where api.HOST_ID= host.ID ";
        List result = this.sqlQuery(sql);
        return result == null ? new ArrayList() : result;
    }

    public Object[] getApiDetailById(int apiId) {
        String sql = String.format("select ID,HOST_ID,TEMPLATE_ID,API_NAME,KEY_WORDS,INTERCEPT_PARAM,DESCRIPT,ENABLE,FLOW_CONTENT from m_api where ID = '%s'", apiId);
        List apis = this.sqlQuery(sql);
        if (!apis.isEmpty()) {
            return (Object[]) apis.get(0);
        } else {
            return null;
        }
    }

    public void updateWorkFlowById(int apiId, String flow_Content) {
        String sql = String.format("update m_api SET FLOW_CONTENT = '%s' where ID=%s ", flow_Content, apiId);
        this.executeSql(sql);
    }

    public void updateApiByIdExceptWorkFlow(Api api) {
        String sql = String.format("update m_api SET HOST_ID = %s ,TEMPLATE_ID = %s ,API_NAME = '%s',KEY_WORDS ='%s' ,INTERCEPT_PARAM='%s',DESCRIPT ='%s', ENABLE=%s " + " where  ID=%s ", api.getHostId(), api.getTemplateId(), api.getApiName(), api.getKeyWords(), api.getInterceptParam(), api.getDescript(), api.getEnable(), api.getId());
        this.executeSql(sql);
    }

    public void aaa(String aa, String bb, String cc) {

    }

    public void bbb(String aa, String bb, String cc) {

    }

    public void ccc(String aa, String bb) {

    }

}
