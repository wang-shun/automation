package com.gome.test.mock.core.stub;

import com.gome.test.mock.core.handler.HandlerInitializer;

import java.util.Map;

/**
 * Stub base
 *
 * @author hannyu
 *
 * @param <Q>
 * @param <R>
 */
public interface Stub<Q, R> {

    /**
     * 设置监听端口
     *
     * @param port
     *            监听端口
     */
    public void setPort(int port);

    /**
     * 设置监听地址
     *
     * @param address
     *            监听地址
     */
    public void setAddress(String address);

    /**
     * 设置监听多地址
     *
     *            监听多地址
     */
    public void setHostMap(Map<String, String> hostMap);

    /**
     * 设置超时，单位毫秒
     *
     * @param timeoutMillis
     */
    //    public void setTimeoutMillis(int timeoutMillis);

    /**
     * 启动server，开启服务。
     *
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * hold住，一直运行。
     *
     * @throws Exception
     */
    public void hold() throws Exception;

    /**
     * 停止服务
     *
     * @throws Exception
     */
    public void stop() throws Exception;

    /**
     * 关闭所有可能的服务，释放所有的资源，不可再用。
     */
    public void release();

    /**
     * add userInitializer
     *
     * @param userInitializer
     */
    public void userInitializer(HandlerInitializer<R, Q> userInitializer);

}
