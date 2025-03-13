package com.alex.rpc.transmission.netty.server;

import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.handler.RpcReqHandler;
import com.alex.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    private final RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("Receive request: {}", rpcMsg);

        MsgType msgType;
        Object data;
        if (rpcMsg.getMsgType().isHeartbeat()) {
            msgType = MsgType.HEARTBEAT_RESP;
            data = null;
        } else {
            msgType = MsgType.RPC_RESP;
            RpcReq rpcReq = (RpcReq) rpcMsg.getData();
            data = handleRpcReq(rpcReq);
        }

        RpcMsg msg = RpcMsg.builder()
                .reqId(rpcMsg.getReqId())
                .version(VersionType.VERSION1)
                .msgType(msgType)
                .compressType(CompressType.GZIP)
                .serializeType(SerializeType.KRYO)
                .data(data)
                .build();

        ctx.channel()
                .writeAndFlush(msg)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Server Exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean isNeedClose = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE;

        if (!isNeedClose) {
            super.userEventTriggered(ctx, evt);
            return;
        }

        log.debug("Server haven't received heartbeat request from client, close channel, address: {}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    private RpcResp<?> handleRpcReq(RpcReq rpcReq) {
        try {
            Object object = rpcReqHandler.invoke(rpcReq);
            return RpcResp.success(rpcReq.getReqId(),  object);
        } catch (Exception e) {
            log.info("Fail to invoke RpcReqHandler, ", e);
            return RpcResp.fail(rpcReq.getReqId(),   e.getMessage());
        }
    }
}
