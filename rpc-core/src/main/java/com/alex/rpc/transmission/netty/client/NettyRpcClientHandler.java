package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("Received RpcResp data: {}", rpcMsg);

        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();

        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResp);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client Exception:", cause);
        ctx.close();
    }
}
