package com.alex.client.nettytest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        // Client Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        // Configure thread group
        bootstrap.group(new NioEventLoopGroup());
        // Specify IO type as NIO
        bootstrap.channel(NioSocketChannel.class);
        // Configure IO processor
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // Add string decoder (Byte -> Object)
                ch.pipeline().addLast(new StringDecoder());
                // Add string encoder (Object -> Byte)
                ch.pipeline().addLast(new StringEncoder());

                ch.pipeline().addLast(new MyClientHandler());
            }
        });
        // Establishing a connection
        Channel channel = bootstrap.connect("127.0.0.1", 8081).channel();


    }

    // Client processor class
    public static class MyClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // After the connection is established, send a message to the server every 5 seconds
            // while (true) {
            //     ctx.channel().writeAndFlush("hello world..");
            //     TimeUnit.SECONDS.sleep(5);
            // }

            // After establishing the connection, send a message to the server once
            String msg = "client msg 1";
            System.out.println("Client sending message: " + msg);
            ctx.channel().writeAndFlush(msg);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("Client received message: " + msg);
        }
    }
}