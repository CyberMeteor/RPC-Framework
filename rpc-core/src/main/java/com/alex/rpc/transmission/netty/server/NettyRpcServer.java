package com.alex.rpc.transmission.netty.server;

import com.alex.rpc.config.RpcServiceConfig;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.provider.ServiceProvider;
import com.alex.rpc.provider.impl.ZkServiceProvider;
import com.alex.rpc.transmission.RpcServer;
import com.alex.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.alex.rpc.transmission.netty.codec.NettyRpcEncoder;
import com.alex.rpc.util.ShutdownHookUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {
    private final ServiceProvider serviceProvider;
    private final int port;

    public NettyRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(int port) {
        this(SingletonFactory.getInstance(ZkServiceProvider.class), port);
    }

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this(serviceProvider, RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(ServiceProvider serviceProvider, int port) {
        this.serviceProvider = serviceProvider;
        this.port = port;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new NettyRpcEncoder());
                            channel.pipeline().addLast(new NettyRpcDecoder());
                            channel.pipeline().addLast(new NettyRpcServerHandler());
                        }
                    });

            ShutdownHookUtils.clearAll();
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("NettyRpcServer started on port:{}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Server error.", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
