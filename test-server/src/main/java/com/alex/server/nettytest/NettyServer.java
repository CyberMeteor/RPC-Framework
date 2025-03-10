package com.alex.server.nettytest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    public static void main(String[] args) {
        // Create a server startup bootstrap
        ServerBootstrap bootstrap = new ServerBootstrap();

        // Configure thread model
        NioEventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);

        // Specify the IO model of the server
        bootstrap.channel(NioServerSocketChannel.class);

        // Define Processor Handler
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                // Add string decoder (Byte -> Object)
                ch.pipeline().addLast(new StringDecoder());
                // Add string encoder (Object -> Byte)
                ch.pipeline().addLast(new StringEncoder());

                // Add custom business logic processing
                ch.pipeline().addLast(new MyServerHandler());
            }
        });

        // Bind port 8081
        bootstrap.bind(8081);
    }

    public static class MyServerHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("Server received message: " + msg);

            String sendMsg = "server msg 1";
            System.out.println("Server sending message: " + sendMsg);
            ctx.channel().writeAndFlush(sendMsg);
        }
    }
}