package com.alex.rpc.transmission.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public NettyRpcDecoder() {
        this (0,0,0);
    }

    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
