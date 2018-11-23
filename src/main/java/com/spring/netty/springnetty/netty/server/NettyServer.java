package com.spring.netty.springnetty.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.logging.SocketHandler;

/**
 * @ClassName NettyServer
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/21 16:57
 * @Version 1.0
 **/
public class NettyServer {

    private static final EventLoopGroup boosGroup =new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
    private static final EventLoopGroup workGroup =new NioEventLoopGroup(100);

    public void start(){
        ServerBootstrap server=new ServerBootstrap();
        server.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new MyServerHandler());
                    }
                });
        try {
            ChannelFuture future=server.bind("127.0.0.1",9362).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new NettyServer().start();
    }
}
