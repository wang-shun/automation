package com.gome.test.mock.db.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gome.test.mock.db.base.BaseService;
import com.gome.test.mock.domain.bean.Host;
import com.gome.test.mock.domain.vo.HostVo;
import com.gome.test.mock.exception.MyException;

@Service("hostService")
public class HostServiceImpl extends BaseService implements HostService {

    @Override
    public void addHost(Host host) throws MyException {
        this.daoManager.getHostDao().save(host);
    }

    @Override
    public Map<String, String> queryHosts() throws MyException {
        return this.daoManager.getHostDao().queryHosts();
    }

    @Override
    public List<HostVo> queryHostVos() throws MyException {
        return this.daoManager.getHostDao().queryHostVos();
    }

    @Override
    public List<HostVo> queryHostVosByUrlPort(String url, int port) throws MyException {
        return this.daoManager.getHostDao().queryHostVosByUrlPort(url, port);
    }

    @Override
    public List<HostVo> queryHostVosByDomainUrlPort(String domain, String url, int port) throws MyException {
        return this.daoManager.getHostDao().queryHostVosByUrlPort(domain, url, port);
    }
}
