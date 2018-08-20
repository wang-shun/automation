package com.gome.test.mock.utils;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * 随机数辅助类
 * 
 */
public class RandomUtil {
    
    
    /**
     * = ThreadLocalRandom.current().nextInt(max)
     * 
     * @param max
     * @return
     */
    public static final int nextInt(int max){
        return ThreadLocalRandom.current().nextInt(max);
    }
    
    
}
