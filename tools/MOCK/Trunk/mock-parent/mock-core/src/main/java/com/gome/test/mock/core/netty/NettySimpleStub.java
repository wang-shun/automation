package com.gome.test.mock.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gome.test.mock.cnst.Ak47Constants;
import com.gome.test.mock.config.BaseConfig;
import com.gome.test.mock.core.HandlerChain;
import com.gome.test.mock.core.Pipe;
import com.gome.test.mock.core.Service;
import com.gome.test.mock.core.handler.CodecStubHandler;
import com.gome.test.mock.core.handler.FilterStubHandler;
import com.gome.test.mock.core.handler.HandlerInitializer;
import com.gome.test.mock.core.handler.LoggingTrafficHandler;
import com.gome.test.mock.core.handler.ServiceStubHandler;
import com.gome.test.mock.core.stub.SimpleStub;
import com.gome.test.mock.utils.Logger;

/**
 * 简单Server
 *
 * @param <Q>
 * @param <R>
 * @author wyhanyu
 */
public class NettySimpleStub<Q, R> implements SimpleStub<Q, R> {
    private static final Logger log = new Logger(NettySimpleStub.class);

    private int port;

    private String address;

    private Map<String, String> hostMap;

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setHostMap(Map<String, String> hostMap) {
        this.hostMap = hostMap;
    }

    protected Pipe<Q, R> pipe;
    protected HandlerInitializer<R, Q> stubInitializer;
    protected HandlerInitializer<R, Q> userInitializer;

    @Override
    public void userInitializer(HandlerInitializer<R, Q> userInitializer) {
        this.userInitializer = userInitializer;
    }

    private ServiceStubHandler<R, Q> serviceStubHandler;

    @Override
    public void addService(String name, Service<Q, R> service) {
        this.serviceStubHandler.addService(name, service);
    }

    @Override
    public void removeService(String name) {
        this.serviceStubHandler.removeService(name);
    }

    private ServerBootstrap serverBootstrap;
    private ChannelFuture bindFuture;

    public NettySimpleStub(final Pipe<Q, R> pipe) {
        this.pipe = pipe;
        this.initStubHandler();
    }

    protected void initStubHandler() {

        final LoggingTrafficHandler<R, Q> logTrafficHandler = new LoggingTrafficHandler<R, Q>();
        final CodecStubHandler<R, Q> codecStubHandler = new CodecStubHandler<R, Q>(this.pipe);
        final FilterStubHandler<R, Q> filterStubHandler = new FilterStubHandler<R, Q>(this.pipe);
        this.serviceStubHandler = new ServiceStubHandler<R, Q>();

        this.stubInitializer = new HandlerInitializer<R, Q>() {
            @Override
            public void initHandler(HandlerChain<R, Q> chain) {
                chain.addLast("LoggingTrafficHandler", logTrafficHandler);
                chain.addLast("CodecStubHandler", codecStubHandler);
                chain.addLast("FilterStubHandler", filterStubHandler);
                chain.addLast("ServiceStubHandler", NettySimpleStub.this.serviceStubHandler);
            }
        };
    }

    protected void initServerBootstrap() {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_RCVBUF, Ak47Constants.SO_RCVBUF).option(ChannelOption.SO_SNDBUF, Ak47Constants.SO_SNDBUF).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.SO_BACKLOG, Ak47Constants.SO_BACKLOG);

        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

                NettyChannel<R, Q> channel = new NettyChannel<R, Q>(ch);
                channel.chain().addLast("StubInitializer", NettySimpleStub.this.stubInitializer);
                if (NettySimpleStub.this.userInitializer != null) {
                    channel.chain().addLast("UserInitializer", NettySimpleStub.this.userInitializer);
                }

                NettyChannelHandlerAdapter<R, Q> nettyChannelHandler = new NettyChannelHandlerAdapter<R, Q>(channel, NettySimpleStub.this.pipe, NettySimpleStub.this.pipe.newExecutor());

                ch.pipeline().addLast("Ak47ChannelHandlerAdapter", nettyChannelHandler);
            }
        });

        log.debug("Stub init success.");
    }

    @Override
    public void start() throws Exception {
        this.initServerBootstrap();
        //bindFuture = serverBootstrap.bind(port);

        //加载地址和端口
        if (this.port <= 0) {
            if (this.hostMap == null || this.hostMap.size() == 0) {
                this.hostMap = BaseConfig.getDefaultMap();
            }
            //监听多地址多端口
            for (String key : this.hostMap.keySet()) {
                try {
                    this.bindFuture = this.serverBootstrap.bind(key, Integer.valueOf(this.hostMap.get(key))).sync();
                } catch (Exception e) {
                    log.info("Address:" + key + " Port: " + this.hostMap.get(key) + " 不可用...");
                    System.out.println("Address:" + key + " Port: " + this.hostMap.get(key) + " 不可用...");
                }
                System.out.println("Host Address is " + key + "+ listening on " + this.hostMap.get(key) + "...");
                log.info("Host Address is " + key + " listening on " + this.hostMap.get(key) + "...");
            }
        } else if (StringUtils.isNotEmpty(this.address) && this.port > 0) {
            try {
                this.bindFuture = this.serverBootstrap.bind(this.address, this.port).sync();
            } catch (Exception e) {
                log.info("Address:" + this.address + " Port: " + this.port + " 不可用...");
                System.out.println("Address:" + this.address + " Port: " + this.port + " 不可用...");
            }
            System.out.println("Host Address is " + this.address + "+ listening on " + this.port + "...");
        } else {
            this.bindFuture = this.serverBootstrap.bind(this.port).sync();
        }
    }

    @Override
    public void stop() {
        if (this.bindFuture != null) {
            Channel ch = this.bindFuture.channel();
            if (ch != null && ch.isActive()) {
                this.bindFuture.channel().close();
            }
        }
    }

    @Override
    public void hold() throws InterruptedException {
        this.bindFuture.sync().channel().closeFuture().sync();
    }

    @Override
    public void release() {
        this.stop();
        this.serverBootstrap.childGroup().shutdownGracefully();
        this.serverBootstrap.group().shutdownGracefully();
        this.serverBootstrap = null;
    }
}
