package com.gome.test.mock.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Network-related helper class
 * 
 * 网络辅助类
 * 
 */
public class NetworkUtil {
    
    /**
     * Get local IP
     * 
     * @return
     * @throws UnknownHostException
     */
    public static String getLocalIp() throws UnknownHostException{
        String ip = InetAddress.getLocalHost().getHostAddress();
        return ip;
    }
}
