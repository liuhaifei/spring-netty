package com.spring.netty.springnetty.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.SocketHandler;

/**
 * @ClassName NettyClient
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/21 17:41
 * @Version 1.0
 **/
public class NettyClient {

    private static final EventLoopGroup group=new NioEventLoopGroup();

    private MyClientHandler myClientHandler=new MyClientHandler();

    private  ChannelFuture future;


    public void start() throws IOException {
        Bootstrap client=new Bootstrap();
        client.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(myClientHandler);
                    }
                });
        try {
             future=client.connect("127.0.0.1",9362).sync();
            Channel channel=future.channel();

//            ChannelFuture lastWrite=null;
//            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
//            //自循
//            for(;;){
//                String message=bufferedReader.readLine();
//                if(message==null)break;
//                lastWrite=channel.writeAndFlush(message+"/r/n");
//                if("bye".equals(message.toLowerCase())){
//                    channel.closeFuture().sync();
//                    break;
//                }
//                if(lastWrite!=null){
//                    lastWrite.sync();
//                }
//            }
            for(int i=0;i<5;i++){
                ChannelFuture future=channel.writeAndFlush("hello x "+i);
                if (future!=null){
                    future.sync();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws IOException {
        NettyClient client=   new NettyClient();
        client.start();

    }
}
