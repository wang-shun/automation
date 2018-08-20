package com.gome.test.mock.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.gome.test.mock.db.base.BaseDaoHibernate;
import com.gome.test.mock.domain.bean.Host;
import com.gome.test.mock.domain.bean.Port;
import com.gome.test.mock.domain.vo.HostVo;
import com.gome.test.mock.exception.MyException;

@Component("hostDao")
public class HostDao extends BaseDaoHibernate<Host, Integer> {

    @SuppressWarnings("unchecked")
    public Map<String, String> queryHosts() throws MyException {
        String hql = "select a.domain,b.portNumber from Host a,Port b where a.portId = b.id and a.enable = 1 and b.enable = 1";
        Map<String, String> hostMap = new HashMap<String, String>();
        List<Object> list = (List<Object>) this.findByHql(hql, new Object[] {});
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
        String hql = "select a,b  from Host a,Port b where a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<Object> objs = (List<Object>) this.findByHql(hql, new Object[] {});
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
    public List<HostVo> queryHostVosByUrlPort(String url, int port) {
        String hql = "select a,b from Host a,Port b where a.url = ? and a.port = ? and a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<HostVo> hostList = (List<HostVo>) this.findByHql(hql, new Object[] { url, port });
        return hostList;
    }

    @SuppressWarnings("unchecked")
    public List<HostVo> queryHostVosByUrlPort(String domain, String url, int port) {
        String hql = "select a,b from Host a,Port b where a.domain = ? and a.url = ? and a.port = ? and a.portId = b.id and a.enable = 1 and b.enable = 1";
        List<HostVo> hostList = (List<HostVo>) this.findByHql(hql, new Object[] { domain, url, port });
        return hostList;
    }

    public int getRandom(int n) {
        Random r = new Random();
        return Math.abs(r.nextInt());
    }

    public static void main(String[] args) {
        HostDao hDao = new HostDao();

    }
}
