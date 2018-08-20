package com.gome.test.mock.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.gome.test.mock.dao.base.BaseDao;
import com.gome.test.mock.exception.MyException;
import com.gome.test.mock.model.bean.Host;
import com.gome.test.mock.model.bean.Port;
import com.gome.test.mock.model.vo.HostVo;

/**
 * Created by Administrator on 2015/10/14.
 */
@Repository
@Transactional
public class HostDao extends BaseDao<Host> {
    public List<Host> getHostList() {
        String sql = "select ID,DOMAIN,PORT_ID,SERVICE_NAME,URL,HOST_TYPE,PROTOCOL_TYPE,ENABLE from m_host ";
        List<Host> apis = this.sqlQuery(sql);
        return apis;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> queryHosts() throws MyException {
        String hql = "select a.domain,b.portNumber from Host a,Port b where a.portId = b.id and a.enable = 1 and b.enable = 1";
        Map<String, String> hostMap = new HashMap<String, String>();
        List<Object> list = this.find(hql);
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                if (obj != null) {
                    Object[] arr = (Object[]) obj;
                    hostMap.put(String.valueOf(arr[0]), String.valueOf(arr[1]));
                }
            }
        }
        return hostMap;
    }

    @SuppressWarnings("unchecked")
    public List<HostVo> queryHostVos() throws MyException {
        List<HostVo> list = null;
        String hql = "select a,b from Host a,Port b where a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<Object> objs = this.find(hql);
        if (objs != null && objs.size() > 0) {
            list = new ArrayList<HostVo>();
            HostVo hostVo = null;
            for (Object object : objs) {
                Object[] objArr = (Object[]) object;
                hostVo = new HostVo();
                hostVo.setHost((Host) objArr[0]);
                hostVo.setPort((Port) objArr[1]);
                list.add(hostVo);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<HostVo> queryHostVosByUrlPort(String url, int port) throws MyException {
        List<HostVo> list = null;
        String hql = "select a,b from Host a,Port b where a.url = ? and b.portNumber = ? and a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<Object> objs = this.find(hql, url, port);
        if (objs != null && objs.size() > 0) {
            list = new ArrayList<HostVo>();
            HostVo hostVo = null;
            for (Object object : objs) {
                Object[] objArr = (Object[]) object;
                hostVo = new HostVo();
                hostVo.setHost((Host) objArr[0]);
                hostVo.setPort((Port) objArr[1]);
                list.add(hostVo);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<HostVo> queryHostVosByHostPort(String host, int port) throws MyException {
        List<HostVo> list = null;
        String hql = "select a,b from Host a,Port b where a.domain = ? and b.portNumber = ? and a.portId = b.id and a.enable = 1 and b.enable = 1 and a.hostType = 1 and a.protocolType <3 ";
        List<Object> objs = this.find(hql, host, port);
        if (objs != null && objs.size() > 0) {
            list = new ArrayList<HostVo>();
            HostVo hostVo = null;
            for (Object object : objs) {
                Object[] objArr = (Object[]) object;
                hostVo = new HostVo();
                hostVo.setHost((Host) objArr[0]);
                hostVo.setPort((Port) objArr[1]);
                list.add(hostVo);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<HostVo> queryHostVosByHostUrlPort(String domain, String url, int port) throws MyException {
        List<HostVo> list = null;
        String hql = "select a,b from Host a,Port b where a.domain = ? and locate(a.url, ? ) > 0 and b.portNumber = ? and a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<Object> objs = this.find(hql, domain, url, port);
        if (objs != null && objs.size() > 0) {
            list = new ArrayList<HostVo>();
            HostVo hostVo = null;
            for (Object object : objs) {
                Object[] objArr = (Object[]) object;
                hostVo = new HostVo();
                hostVo.setHost((Host) objArr[0]);
                hostVo.setPort((Port) objArr[1]);
                list.add(hostVo);
            }
        }
        return list;
    }

    public List queryHostListBySearch() {
        String sql = "select host.ID,host.DOMAIN,host.PORT_ID,host.SERVICE_NAME,host.URL,host.HOST_TYPE,host.PROTOCOL_TYPE, host.ENABLE ,port.PORT_NUMBER from m_host host , m_port port where host.PORT_ID=port.ID";
        List hosts = this.sqlQuery(sql);
        return hosts == null ? new ArrayList() : hosts;
    }

    public Object[] getHostDetailById(int hostId) {
        String sql = String.format("select ID,DOMAIN,PORT_ID,SERVICE_NAME,URL,HOST_TYPE,PROTOCOL_TYPE,ENABLE from m_host where ID=%s", hostId);
        List hosts = this.sqlQuery(sql);
        if (!hosts.isEmpty()) {
            return (Object[]) hosts.get(0);
        } else {
            return null;
        }
    }

    public List queryApiListByName(String serviceName, String domain, String url) {
        String sql = String.format("select ID,DOMAIN,PORT_ID,SERVICE_NAME,URL,HOST_TYPE,PROTOCOL_TYPE,ENABLE from m_host where SERVICE_NAME='%s' and DOMAIN='%s' and URL='%s'", serviceName, domain, url);
        List<Host> list = this.sqlQuery(sql);
        return list;
    }
}
