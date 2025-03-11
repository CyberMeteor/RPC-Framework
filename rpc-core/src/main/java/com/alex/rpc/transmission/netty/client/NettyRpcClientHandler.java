package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcResp<?>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResp<?> rpcResp) throws Exception {
        log.debug("Received RpcResp data: {}", rpcResp);
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResp);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Server Exception:", cause);
        ctx.close();
    }
}
