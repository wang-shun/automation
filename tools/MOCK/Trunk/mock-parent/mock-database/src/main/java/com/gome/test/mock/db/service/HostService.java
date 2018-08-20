package com.gome.test.mock.db.service;

import java.util.List;
import java.util.Map;

import com.gome.test.mock.domain.bean.Host;
import com.gome.test.mock.domain.vo.HostVo;
import com.gome.test.mock.exception.MyException;

public interface HostService {

    /** 添加主机 **/
    public void addHost(Host host) throws MyException;

    /** 查询主机<域名,端口> **/
    public Map<String, String> queryHosts() throws MyException;

    /** 查询主机列表 **/
    public List<HostVo> queryHostVos() throws MyException;

    /** 查询主机列表 **/
    public List<HostVo> queryHostVosByUrlPort(String url, int port) throws MyException;

    /** 查询主机列表 **/
    public List<HostVo> queryHostVosByDomainUrlPort(String domain, String url, int port) throws MyException;
}
