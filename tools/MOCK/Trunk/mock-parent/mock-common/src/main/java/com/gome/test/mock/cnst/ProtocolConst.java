package com.gome.test.mock.cnst;

import java.util.HashMap;
import java.util.Map;

public class ProtocolConst {

    //1:Http   2: WebService    3:Dubbo    4:Socket
    public static final String HTTP_PROTOCOL_MARK = "Http";

    public static final String WEB_SERVICE_PROTOCOL_MARK = "WebService";

    public static final String DUBBO_PROTOCOL_MARK = "Dubbo";

    public static final String SOCKET_PROTOCOL_MARK = "Socket";

    public static final int HTTP_PROTOCOL_CODE = 1;

    public static final int WEB_SERVICE_PROTOCOL_CODE = 2;

    public static final int DUBBO_PROTOCOL_CODE = 3;

    public static final int SOCKET_PROTOCOL_CODE = 4;

    public static final Map<Integer, String> PROTOCOL_CODES = new HashMap<Integer, String>();

    static {
        PROTOCOL_CODES.put(HTTP_PROTOCOL_CODE, HTTP_PROTOCOL_MARK);
        PROTOCOL_CODES.put(WEB_SERVICE_PROTOCOL_CODE, WEB_SERVICE_PROTOCOL_MARK);
        PROTOCOL_CODES.put(DUBBO_PROTOCOL_CODE, DUBBO_PROTOCOL_MARK);
        PROTOCOL_CODES.put(SOCKET_PROTOCOL_CODE, SOCKET_PROTOCOL_MARK);
    }

}
