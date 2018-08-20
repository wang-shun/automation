package com.gome.test.mock.core.driver;


import com.gome.test.mock.core.stress.BenchResult;
import com.gome.test.mock.core.stress.Clip;
import com.gome.test.mock.core.stress.Collector;

/**
 * 压力Driver
 * 
 * Example:
 *  TODO:
 * 
 * @author wyhanyu
 *
 * @param <Q>
 * @param <R>
 */
public interface StressDriver<Q, R> extends Driver<Q, R> {
    
    public void loadClip(Clip<Q> clip);
    
    public void newConnection();
    
    public BenchResult bench();
    
    public BenchResult bench(int concurrency);
    
    public BenchResult bench(int concurrency, boolean keepalive);
    
    public BenchResult bench(int concurrency, boolean keepalive, int tpsLimit);
    
    public boolean isOver();
    
    public void hold();
    
    public void holdUntilFiredNum(long num);
    
    public void holdUntilSeconds(int seconds);
    
    public void setCollector(Collector<Q, R> collector);

    
    
    
    
    
}
