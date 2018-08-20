package com.gome.test.mock.model.vo;


import com.gome.test.mock.model.bean.Host;
import com.gome.test.mock.model.bean.Port;

public class HostVo implements java.io.Serializable {

    private static final long serialVersionUID = 3191799006029489978L;

    private Host host;
    private Port port;

    public Host getHost() {
        return this.host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Port getPort() {
        return this.port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

}
