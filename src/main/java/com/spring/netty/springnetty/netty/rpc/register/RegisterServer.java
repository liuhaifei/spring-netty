package com.spring.netty.springnetty.netty.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @ClassName RegisterServer
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 15:07
 * @Version 1.0
 **/
public class RegisterServer {

    private static final EventLoopGroup boosGroup=new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
    private static final EventLoopGroup workGroup=new NioEventLoopGroup(100);

    public void start(){
        ServerBootstrap server=new ServerBootstrap();
        server.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast("encoder",new ObjectEncoder())
                                .addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast(new RegisterHandler());

                    }
                });
        try {
            ChannelFuture future=server.bind("127.0.0.1",9630).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new RegisterServer().start();
    }
}
