package com.spring.netty.springnetty.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @ClassName MyServerHandler
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/21 17:32
 * @Version 1.0
 **/
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接进入>>>>>>>");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受消息>>>>>:"+msg);
        String response=null;
        boolean close=false;
        if(msg.toString().isEmpty()){
            response="please say something /r/n";
        }else if("bye".equals(msg.toString().toLowerCase())){
            response="good bye /r/n";
            close=true;
        }else {
            response="did you have say "+msg.toString() +"?/r/n";
        }
        ChannelFuture future=ctx.writeAndFlush(response);
        if(close){
           future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("异常>>>>>>:"+cause.getMessage());
    }
}
