package com.gome.test.mock.core;

import java.util.List;

/**
 * buffer拆分器
 * 
 * 在单一长连接中，Netty只为一个连接分配一个IO线程，最大利用线程池的优势，可以把Buffer分成多份，分给不同的线程处理。
 * 
 * @author hannyu
 *
 */
public interface Spliter {

    public List<Buffer> split(Buffer buf);
    
}
