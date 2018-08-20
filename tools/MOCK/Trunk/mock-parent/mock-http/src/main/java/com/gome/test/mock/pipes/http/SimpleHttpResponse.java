package com.gome.test.mock.pipes.http;

import com.gome.test.mock.cnst.Ak47Constants;
import com.gome.test.mock.utils.Logger;

/**
 * 简单的HttpResponse实现
 *
 *          Response = Status-Line
 *(( general-header
                        | response-header
                        | entity-header ) CRLF)
                       CRLF
                       [ message-body ]
 *
 *
 *
 */
public class SimpleHttpResponse extends SimpleHttpMessage {
    private static final Logger log = new Logger(SimpleHttpResponse.class);

    private String httpVersion = "HTTP/1.1";
    private String statusCode = "200";
    private String reasonPhrase = "OK\r\nContent-Type:text/html;charset=utf-8";


    /**
     * A Simple Http Response with default params.
     */
    public SimpleHttpResponse() {
        this(null, null, null);
        this.addHeader("Server", Ak47Constants.NAME + "/" + Ak47Constants.VERSION);
    }

    /**
     * A Simple Http Response with given statusCode.
     *
     * @param statusCode        Status code, such as 200
     */
    public SimpleHttpResponse(String statusCode) {
        this(statusCode, null, null);
    }

    /**
     * A Simple Http Response with given statusCode and reasonPhrase.
     *
     * @param statusCode        Status code, such as 200
     * @param reasonPhrase      Reason phrase
     */
    public SimpleHttpResponse(String statusCode, String reasonPhrase) {
        this(statusCode, reasonPhrase, null);
    }

    /**
     * A Simple Http Response with given statusCode, reasonPhrase and httpVersion.
     *
     * @param statusCode        Status code, such as 200
     * @param reasonPhrase      Reason phrase, such as OK
     * @param httpVersion       Http version
     */
    public SimpleHttpResponse(String statusCode, String reasonPhrase, String httpVersion) {
        this.setResponseLine(statusCode, reasonPhrase, httpVersion);
    }

    /**
     *
     * @return          Http version
     */
    public String getHttpVersion() {
        return this.httpVersion;
    }

    /**
     *
     * @param httpVersion   Http version
     */
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
        this.updateStartLine();
    }

    /**
     *
     * @return              Status code, such as 200
     */
    public String getStatusCode() {
        return this.statusCode;
    }

    /**
     *
     * @param statusCode    Status code, such as 200
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        this.updateStartLine();
    }

    /**
     *
     * @return              Reason phrase, such as OK
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     *
     * @param reasonPhrase  Reason phrase, such as OK
     */
    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
        this.updateStartLine();
    }

    /**
     * set Response-Line
     *
     * @param statusCode        Status code, such as 200
     * @param reasonPhrase      Reason phrase, such as OK
     * @param httpVersion       Http version
     */
    public void setResponseLine(String statusCode, String reasonPhrase, String httpVersion) {
        this.statusCode = statusCode == null ? this.statusCode : statusCode;
        this.reasonPhrase = reasonPhrase == null ? this.reasonPhrase : reasonPhrase;
        this.httpVersion = httpVersion == null ? this.httpVersion : httpVersion;
        this.updateStartLine();
    }

    private void updateStartLine() {
        this.setStartLine(this.httpVersion + " " + this.statusCode + " " + this.reasonPhrase);
    }

    /**
     * 转化，如果格式不对则返回null
     *
     * @param shm       SimpleHttpMessage
     * @return          HttpResponse or null if Response-Line malformed
     */
    public static SimpleHttpResponse valueOf(SimpleHttpMessage shm) {
        String startLine = shm.getStartLine();
        String[] startLineItems = startLine.split(" ", 3);
        if (startLineItems.length != 3) {
            log.warn("Malformed first line.");
            return null;
        } else if (!startLineItems[0].startsWith("HTTP")) {
            log.warn("Malformed first line, may NOT http.");
            return null;
        }
        SimpleHttpResponse shr = new SimpleHttpResponse();
        shr.setResponseLine(startLineItems[0], startLineItems[1], startLineItems[2]);
        shr.setHeaders(shm.getHeaders());
        shr.setContent(shm.getContent());
        return shr;
    }

    /**
     *
     * Deep copy with Response-Line, headers and body.
     *
     * @param response      HttpResponse
     */
    public void copyOf(SimpleHttpResponse response) {
        this.setResponseLine(response.getStatusCode(), response.getReasonPhrase(), response.getHttpVersion());
        this.setHeaders(response.getHeaders());
        this.setContent(response.getContent());
    }

}
