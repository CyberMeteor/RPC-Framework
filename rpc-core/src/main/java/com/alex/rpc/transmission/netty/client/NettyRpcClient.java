package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.alex.rpc.transmission.netty.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,  DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }


    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", RpcConstant.SERVER_PORT).sync();

        log.info("NettyRpcClient connected to XXX");

        Channel channel = channelFuture.channel();
        String interfaceName = req.getInterfaceName();
        channel.writeAndFlush(interfaceName).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        // Block and wait until it is closed
        channel.closeFuture().sync();

        // Obtain data from server
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);

        String s = channel.attr(key).get();
        System.out.println(s);
        return null;
    }
}
