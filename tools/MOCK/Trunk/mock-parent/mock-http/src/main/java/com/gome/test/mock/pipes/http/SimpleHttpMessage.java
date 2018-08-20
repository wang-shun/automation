package com.gome.test.mock.pipes.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gome.test.mock.cnst.Ak47Constants;
import com.gome.test.mock.utils.ByteUtil;

/**
 * 简单的HttpRequest实现
 *
 */
public class SimpleHttpMessage {

    // HttpMessage = startLine + headers + content;
    // first line
    private String startLine;
    // many headers
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();;
    // any content
    private byte[] content;

    public String getStartLine() {
        return this.startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * 添加一个Header
     *
     * @param key       Key in header
     * @param value     Value of the key
     */
    public void addHeader(String key, String value) {
        //        key = StringUtil2.asciiToLowerCase(key);
        List<String> vl = this.headers.get(key);
        if (null == vl) {
            vl = new ArrayList<String>(1);
            vl.add(value);
            this.headers.put(key, vl);
        } else {
            vl.add(value);
        }
    }

    /**
     * 获取该key的第一个header
     * 若不存在，则返回null
     *
     * @param key       Key in header
     * @return          Value of the key
     */
    public String getHeaderFirst(String key) {
        //        key = StringUtil2.asciiToLowerCase(key);
        List<String> vl = this.headers.get(key);
        if (null == vl) {
            return null;
        } else {
            return vl.get(0);
        }
    }

    /**
     * 修改key的header
     *
     * @param key       Key in header
     * @param value     Value of the Key
     */
    public void setOrAddFirstHeader(String key, String value) {
        //        key = StringUtil2.asciiToLowerCase(key);
        List<String> vl = this.headers.get(key);
        if (null == vl) {
            this.addHeader(key, value);
        } else {
            vl.set(0, value);
        }
    }

    /**
     * build HTTP header
     *
     * @return      Text of header
     */
    public String buildHeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.startLine).append(SimpleHttpParser.CRLF_STRING);
        for (String key : this.headers.keySet()) {
            List<String> vl = this.headers.get(key);
            for (String v : vl) {
                if (key.equals("content-length")) {
                    key = "Content-Length";
                }
                if (key.equals("server")) {
                    key = "Server";
                }
                sb.append(key).append(": ").append(v).append(SimpleHttpParser.CRLF_STRING);

            }
        }
        sb.append(SimpleHttpParser.CRLF_STRING);
        return sb.toString();
    }

    /**
     * 完整打印
     *
     * @return      Text of header, and size of body
     */
    @Override
    public String toString() {
        if (null != this.content) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.buildHeaderString());
            sb.append("content(" + this.content.length + ")");
            return sb.toString();
        } else {
            return this.buildHeaderString();
        }
    }

    /**
     * 导出为bytearray
     *
     * @return      Bytes of all header and body
     */
    public byte[] buildFullBytes() throws Exception {
        byte[] half = this.buildHeaderString().getBytes(Ak47Constants.DEFAULT_ENCODING);
        if (null != this.content) {
            byte[] full = ByteUtil.merge(half, this.content);
            return full;
        } else {
            return half;
        }
    }
}
