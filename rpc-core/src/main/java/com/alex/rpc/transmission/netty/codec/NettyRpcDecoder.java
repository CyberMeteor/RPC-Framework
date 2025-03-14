package com.alex.rpc.transmission.netty.codec;

import cn.hutool.core.util.ArrayUtil;
import com.alex.rpc.compress.Compress;
import com.alex.rpc.compress.impl.GzipCompress;
import com.alex.rpc.constant.RpcConstant;
import com.alex.rpc.dto.RpcMsg;
import com.alex.rpc.dto.RpcReq;
import com.alex.rpc.dto.RpcResp;
import com.alex.rpc.enums.CompressType;
import com.alex.rpc.enums.MsgType;
import com.alex.rpc.enums.SerializeType;
import com.alex.rpc.enums.VersionType;
import com.alex.rpc.exception.RpcException;
import com.alex.rpc.factory.SingletonFactory;
import com.alex.rpc.serialize.Serializer;
import com.alex.rpc.serialize.impl.KryoSerializer;
import com.alex.rpc.spi.CustomLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public NettyRpcDecoder() {
        super(RpcConstant.REQ_MAX_LEN, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        return decodeFrame(frame);
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        readAndCheckMagicCode(byteBuf);
        byte versionCode = byteBuf.readByte();
        VersionType version = VersionType.from(versionCode);

        int msgLen = byteBuf.readInt();

        byte msgTypeCode = byteBuf.readByte();
        MsgType msgType = MsgType.from(msgTypeCode);

        byte serializerTypeCode = byteBuf.readByte();
        SerializeType serializeType = SerializeType.from(serializerTypeCode);

        byte compressTypeCode = byteBuf.readByte();
        CompressType compressType = CompressType.from(compressTypeCode);

        int reqId = byteBuf.readInt();

        Object data = readData(byteBuf, msgLen - RpcConstant.REQ_HEAD_LEN, msgType, serializeType);

        return RpcMsg.builder()
                .reqId(reqId)
                .msgType(msgType)
                .version(version)
                .compressType(compressType)
                .serializeType(serializeType)
                .data(data)
                .build();
    }

    private void readAndCheckMagicCode(ByteBuf byteBuf) {
        byte[] magicBytes = new byte[RpcConstant.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);

        if (!ArrayUtil.equals(magicBytes, RpcConstant.RPC_MAGIC_CODE)) {
            throw new RpcException("Magic bytes exception: " + new String(magicBytes));
        }
    }

    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType, SerializeType serializeType) {
        if (msgType.isReq()) {
            return readData(byteBuf, dataLen, RpcReq.class, serializeType);
        }
        return readData(byteBuf, dataLen, RpcResp.class, serializeType);
    }

    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz, SerializeType serializeType) {
        if (dataLen <= 0) {
            return null;
        }

        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        data = compress.decompress(data);

        String serializerTypeStr = serializeType.getDesc();

        Serializer serializer = CustomLoader.getLoader(Serializer.class).get(serializerTypeStr);

        return serializer.deserialize(data, clazz);
    }
}
