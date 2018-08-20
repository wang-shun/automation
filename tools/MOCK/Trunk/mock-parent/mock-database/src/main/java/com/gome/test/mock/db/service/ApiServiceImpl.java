package com.gome.test.mock.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gome.test.mock.db.base.BaseService;
import com.gome.test.mock.domain.bean.Api;
import com.gome.test.mock.domain.vo.HostVo;
import com.gome.test.mock.exception.MyException;

/**
 * Created by chaizhongbao on 2015/9/25.
 */
@Service("apiService")
public class ApiServiceImpl extends BaseService implements ApiService {
    @Override
    public void addApi(Api model) throws MyException {

    }

    @Override
    public List<Api> queryApis() throws MyException {
        return this.daoManager.getApiDao().queryApis();
    }

    @Override
    public List<Api> queryApiList(String url, int port) throws MyException {
        List<HostVo> hostVos = this.daoManager.getHostDao().queryHostVosByUrlPort(url, port);
        List<Integer> hostIds = new ArrayList<Integer>();
        if (hostVos != null && hostVos.size() > 0) {
            for (HostVo hostVo : hostVos) {
                if (hostVo != null) {
                    if (!hostIds.contains(hostVo.getHost().getId())) {
                        hostIds.add(hostVo.getHost().getId());
                    }
                }
            }
        }
        return this.daoManager.getApiDao().queryApiList(hostIds);
    }

    @Override
    public List<Api> queryApiList(String domain, String url, int port) throws MyException {
        List<HostVo> hostVos = this.daoManager.getHostDao().queryHostVosByUrlPort(domain, url, port);
        List<Integer> hostIds = new ArrayList<Integer>();
        if (hostVos != null && hostVos.size() > 0) {
            for (HostVo hostVo : hostVos) {
                if (hostVo != null) {
                    if (!hostIds.contains(hostVo.getHost().getId())) {
                        hostIds.add(hostVo.getHost().getId());
                    }
                }
            }
        }
        return this.daoManager.getApiDao().queryApiList(hostIds);
    }
}
