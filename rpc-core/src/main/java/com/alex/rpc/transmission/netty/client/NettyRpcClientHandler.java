package com.alex.rpc.transmission.netty.client;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType().isHeartbeat()) {
            log.debug("Received heartbeat from server: {}", rpcMsg);
            return;
        }

        log.debug("Received RpcResp data: {}", rpcMsg);
        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();

        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResp);
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedHeartBeat = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;

        if (!isNeedHeartBeat) {
            super.userEventTriggered(ctx, evt);
            return;
        }

        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.HEARTBEAT_REQ)
                .build();

        log.debug("Client send heartbeat req: {}", rpcMsg);
        ctx.writeAndFlush(rpcMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client Exception:", cause);
        ctx.close();
    }
}
