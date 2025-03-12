package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
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

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

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

        RpcMsg rpcMsg = RpcMsg.builder()
                .reqId(ID_GEN.getAndIncrement())
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.RPC_REQ)
                .data(req)
                .build();

        channel.writeAndFlush(rpcMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        // Block and wait until it is closed
        channel.closeFuture().sync();

        // Obtain data from server
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        return channel.attr(key).get();
    }
}
