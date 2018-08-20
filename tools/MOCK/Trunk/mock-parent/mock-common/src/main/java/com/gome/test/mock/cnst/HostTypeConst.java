package com.gome.test.mock.cnst;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/20.
 */
public class HostTypeConst {
    public static final int CLIENT_CODE = 0 ;
    public static final int SERVER_CODE = 1 ;
    public static final String CLIENT_MARK = "Client";
    public static final String SERVER_MARK = "Server";

    public static final Map<Integer, String> HostType_CODES = new HashMap<Integer, String>();

    static {
        HostType_CODES.put(CLIENT_CODE,CLIENT_MARK);
        HostType_CODES.put(SERVER_CODE,SERVER_MARK);
    }

}
