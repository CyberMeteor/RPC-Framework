package com.alex.rpc.transmission.netty.codec;

import com.alex.rpc.compress.Compress;
import com.alex.rpc.compress.impl.GzipCompress;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.serialize.Serializer;
import com.alex.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {


    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        byteBuf.writeByte(rpcMsg.getVersion().getCode());

        // Move 4 bits to the right to make room for datagram
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        byteBuf.writeInt(rpcMsg.getReqId());

        int msgLen = RpcConstant.REQ_HEAD_LEN;
        if (!rpcMsg.getMsgType().isHeartbeat() && !Objects.isNull(rpcMsg.getData())) {
            byte[] data = data2Bytes(rpcMsg);
            byteBuf.writeBytes(data);
            msgLen += data.length;
        }

        int curIdx = byteBuf.writerIndex();
        byteBuf.writerIndex( curIdx - msgLen + RpcConstant.RPC_MAGIC_CODE.length + 1);
        byteBuf.writeInt(msgLen);
        byteBuf.writerIndex(curIdx);
    }

    private byte[] data2Bytes(RpcMsg rpcMsg) {
        // TODO: get Serialize type and Compress type
//        rpcMsg.getSerializeType();
//        rpcMsg.getCompressType();

        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] data = serializer.serialize(rpcMsg.getData());

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        return compress.compress(data);
    }
}
