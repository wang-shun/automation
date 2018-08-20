package com.gome.test.mock.cnst;

import java.util.HashMap;
import java.util.Map;

public class HttpCodeConst {

    /** HTTP常见错误返回代码 */
    public static final Map<String, String> HTTP_STATUS_CODES = new HashMap<String, String>();
    static {
        HTTP_STATUS_CODES.put("100", "Continue");
        HTTP_STATUS_CODES.put("101", "witching Protocols");
        HTTP_STATUS_CODES.put("200", "OK");
        HTTP_STATUS_CODES.put("201", "Created");
        HTTP_STATUS_CODES.put("202", "Accepted");
        HTTP_STATUS_CODES.put("203", "Non-Authoritative Information");
        HTTP_STATUS_CODES.put("204", "No Content");
        HTTP_STATUS_CODES.put("205", "Reset Content");
        HTTP_STATUS_CODES.put("206", "Partial Content");
        HTTP_STATUS_CODES.put("300", "Multiple Choices");
        HTTP_STATUS_CODES.put("301", "Moved Permanently");
        HTTP_STATUS_CODES.put("302", "Found");
        HTTP_STATUS_CODES.put("303", "See Other");
        HTTP_STATUS_CODES.put("304", "Not Modified");
        HTTP_STATUS_CODES.put("305", "Use Proxy");
        HTTP_STATUS_CODES.put("307", "Temporary Redirect");
        HTTP_STATUS_CODES.put("400", "Bad Request");
        HTTP_STATUS_CODES.put("401", "Unauthorized");
        HTTP_STATUS_CODES.put("402", "Payment Required");
        HTTP_STATUS_CODES.put("403", "Forbidden");
        HTTP_STATUS_CODES.put("404", "Not Found");
        HTTP_STATUS_CODES.put("405", "Method Not Allowed");
        HTTP_STATUS_CODES.put("406", "Not Acceptable");
        HTTP_STATUS_CODES.put("407", "Proxy Authentication Required");
        HTTP_STATUS_CODES.put("408", "Request Time-out");
        HTTP_STATUS_CODES.put("409", "Conflict");
        HTTP_STATUS_CODES.put("410", "Gone");
        HTTP_STATUS_CODES.put("411", "Length Required");
        HTTP_STATUS_CODES.put("412", "Precondition Failed");
        HTTP_STATUS_CODES.put("413", "Request Entity Too Large");
        HTTP_STATUS_CODES.put("414", "Request-URI Too Large");
        HTTP_STATUS_CODES.put("415", "Unsupported Media Type");
        HTTP_STATUS_CODES.put("416", "Requested range not satisfiable");
        HTTP_STATUS_CODES.put("417", "Expectation Failed");
        HTTP_STATUS_CODES.put("500", "Internal Server Error");
        HTTP_STATUS_CODES.put("501", "Not Implemented");
        HTTP_STATUS_CODES.put("502", "Bad Gateway");
        HTTP_STATUS_CODES.put("503", "Service Unavailable");
        HTTP_STATUS_CODES.put("504", "Gateway Time-out");
        HTTP_STATUS_CODES.put("505", "HTTP Version not supported");
    }

}
