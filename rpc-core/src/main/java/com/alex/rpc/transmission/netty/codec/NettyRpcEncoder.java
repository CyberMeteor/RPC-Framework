package com.alex.rpc.transmission.netty.codec;

import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        byte version = rpcMsg.getVersion().getCode();
        byteBuf.writeByte(version);

        // Move 4 bits to the right to make room for datagram
        byteBuf.writerIndex(byteBuf.writerIndex() + 1);

        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        byteBuf.writeInt(ID_GEN.getAndIncrement());

    }
}
