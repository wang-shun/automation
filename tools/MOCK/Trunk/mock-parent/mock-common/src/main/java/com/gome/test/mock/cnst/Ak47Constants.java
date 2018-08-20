package com.gome.test.mock.cnst;

/**
 * AK47 constants variables.
 *
 * AK47的常量
 *
 */
public class Ak47Constants {

    //////////////////////// base

    //tls
    public static final String PROTOCOL = "TLS";

    //jks
    public static final String CERT_FORMAT = "JKS";

    // name
    public static final String NAME = "MOCK";

    // version
    public static final String VERSION = "0.1.0";

    // default encoding
    public static final String DEFAULT_ENCODING = "UTF-8";

    ////////////////////// Network

    // socket option determining the number of connections queued
    public static final int SO_BACKLOG = 10000;

    /**
     * The SO_RCVBUF option is used by the platform's networking code as
     * a hint for the size to set the underlying network I/O buffers.
     *
     * Increasing the receive buffer size can increase the performance
     * of network I/O for high-volume connection, while decreasing it can
     * help reduce the backlog of incoming data.
     *
     */
    public static final int SO_RCVBUF = 1024 * 8;

    /**
     * The SO_SNDBUF option is used by the platform's networking code as
     * a hint for the size to set the underlying network I/O buffers.
     */
    public static final int SO_SNDBUF = 1024 * 8;
}
