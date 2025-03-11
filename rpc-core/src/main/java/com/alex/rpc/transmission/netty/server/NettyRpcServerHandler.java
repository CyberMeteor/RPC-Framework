package com.alex.rpc.transmission.netty.server;

import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String str) throws Exception {
        log.debug("Receive request: {}", str);

//        RpcResp<String> rpcResp = RpcResp.success(str, "Simulated response data");
        ctx.channel()
                .writeAndFlush("Simulated response data")
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Server Exception", cause);
        ctx.close();
    }
}
