package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.registry.ServiceDiscovery;
import com.alex.rpc.registry.impl.ZkServiceDiscovery;
import com.alex.rpc.spi.CustomLoader;
import com.alex.rpc.transmission.RpcClient;
import com.alex.rpc.transmission.netty.codec.NettyRpcDecoder;
import com.alex.rpc.transmission.netty.codec.NettyRpcEncoder;
import com.alex.rpc.util.ConfigUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelPool channelPool;

    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.channelPool = SingletonFactory.getInstance(ChannelPool.class);
    }

    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,  DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast((new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS)));
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }


    @SneakyThrows
    @Override
    public Future<RpcResp<?>> sendReq(RpcReq req) {
        CompletableFuture<RpcResp<?>> cf = new CompletableFuture<>();
        UnprocessedRpcReq.put(req.getReqId(), cf);

        InetSocketAddress address = serviceDiscovery.lookupService(req);
        Channel channel = channelPool.get(address, () -> connect(address));
        log.info("Netty rpc client connected to: {}", address);

        String serializer = ConfigUtils.getRpcConfig().getSerializer();

        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.from(serializer))
                .compressType(CompressType.GZIP)
                .msgType(MsgType.RPC_REQ)
                .data(req)
                .build();

        channel.writeAndFlush(rpcMsg)
                .addListener((ChannelFutureListener) listener -> {
                    if (!listener.isSuccess()) {
                        listener.channel().close();
                        cf.completeExceptionally(listener.cause());
                    }
                });

        return cf;
    }

    private Channel connect(InetSocketAddress address) {
        try {
            return bootstrap.connect(address)
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            log.error("Fail to connect to remote server ", e);
            throw new RuntimeException(e);
        }
    }
}
