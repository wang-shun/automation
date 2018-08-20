package com.gome.test.mock.core.netty;

import com.gome.test.mock.cnst.Ak47Constants;
import com.gome.test.mock.core.HandlerChain;
import com.gome.test.mock.core.Pipe;
import com.gome.test.mock.core.Service;
import com.gome.test.mock.core.driver.SimpleDriver;
import com.gome.test.mock.core.handler.*;
import com.gome.test.mock.utils.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NettySimpleDriver<Q, R> implements SimpleDriver<Q, R> {
    private static final Logger log = new Logger(NettySimpleDriver.class);

    protected String addr;
    protected int port;

    @Override
    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    protected Pipe<Q, R> pipe;
    protected HandlerInitializer<Q, R> stubInitializer;
    protected HandlerInitializer<Q, R> userInitializer;

    @Override
    public void userInitializer(HandlerInitializer<Q, R> userInitializer) {
        this.userInitializer = userInitializer;
    }

    private ServiceDriverHandler<Q, R> serviceDriverHandler;

    public void addService(String name, Service<Q, R> service) {
        this.serviceDriverHandler.addService(name, service);
    }

    public void delService(String name) {
        this.serviceDriverHandler.removeService(name);
    }

    protected Bootstrap bootstrap;
    protected Channel channel;
    protected BlockingQueueDriverHandler<Q, R> blockingQueueDriverHandler;

    public NettySimpleDriver(Pipe<Q, R> pipe) {
        this.pipe = pipe;
        this.initDriverHandler();
    }

    protected void initDriverHandler() {

        final LoggingTrafficHandler<Q, R> logTrafficHandler = new LoggingTrafficHandler<Q, R>();
        final CodecDriverHandler<Q, R> codecDriverHandler = new CodecDriverHandler<Q, R>(this.pipe);
        final FilterDriverHandler<Q, R> filterDriverHandler = new FilterDriverHandler<Q, R>(this.pipe);
        this.serviceDriverHandler = new ServiceDriverHandler<Q, R>();
        this.blockingQueueDriverHandler = new BlockingQueueDriverHandler<Q, R>();

        this.stubInitializer = new HandlerInitializer<Q, R>() {
            @Override
            public void initHandler(HandlerChain<Q, R> chain) {
                chain.addLast("LoggingTrafficHandler", logTrafficHandler);
                chain.addLast("CodecDriverHandler", codecDriverHandler);
                chain.addLast("FilterDriverHandler", filterDriverHandler);
                chain.addLast("ServiceDriverHandler", NettySimpleDriver.this.serviceDriverHandler);
                chain.addLast("BlockingQueueDriverHandler", NettySimpleDriver.this.blockingQueueDriverHandler);
            }
        };
    }

    private void initBootstrap() {
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_RCVBUF, Ak47Constants.SO_RCVBUF).option(ChannelOption.SO_SNDBUF, Ak47Constants.SO_SNDBUF).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

                NettyChannel<Q, R> channel = new NettyChannel<Q, R>(ch);
                channel.chain().addLast("StubInitializer", NettySimpleDriver.this.stubInitializer);
                if (NettySimpleDriver.this.userInitializer != null) {
                    channel.chain().addLast("UserInitializer", NettySimpleDriver.this.userInitializer);
                }

                NettyChannelHandlerAdapter<Q, R> nettyChannelHandler = new NettyChannelHandlerAdapter<Q, R>(channel, NettySimpleDriver.this.pipe, NettySimpleDriver.this.pipe.newExecutor());

                ch.pipeline().addLast("NettyChannelHandlerAdapter", nettyChannelHandler);
            }
        });

        log.debug("Driver init success.");
    }

    @Override
    public R send(final Q q) throws Exception {
        if (null == this.bootstrap) {
            this.initBootstrap();
        }

        if (null == q) {
            throw new IllegalArgumentException("Q is null.");
        }

        if (null != this.channel && !this.channel.isActive()) {
            this.channel.close();
            this.channel = null;
        }

        if (this.channel == null) {
            //连接服务，建立通道
            this.channel = this.bootstrap.connect(new InetSocketAddress(this.addr, this.port)).awaitUninterruptibly().channel();
            //            bootstrap
            //                .connect(new InetSocketAddress(addr, port))
            //                .addListener(new ChannelFutureListener(){
            //                    @Override
            //                    public void operationComplete(ChannelFuture future)
            //                            throws Exception {
            //                        if( future.isSuccess() ){
            //                            channel = future.channel();
            //                            blockingQueueDriverHandler.send(q);
            //                        }else{
            //                            throw new Ak47RuntimeException("Fail to connect to "+addr+":"+port);
            //                        }
            //                    }
            //
            //                });
        }
        //发送请求
        this.blockingQueueDriverHandler.send(q);

        return this.blockingQueueDriverHandler.recv();
    }

    @Override
    public R sendAndClose(Q q) throws Exception {
        R r = this.send(q);
        this.close();
        return r;
    }

    @Override
    public void close() throws Exception {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close().sync();
        }
    }

    /**
     * 释放所有资源，不可再使用
     */
    @Override
    public void release() {
        try {
            this.close();
        } catch (Exception e) {}
        this.bootstrap.group().shutdownGracefully();
        this.channel = null;
        this.bootstrap = null;

    }

}
