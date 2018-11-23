package com.spring.netty.springnetty.netty.rpc.consumer;

import com.spring.netty.springnetty.netty.rpc.msg.InvokerMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName RpcProxy
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 16:41
 * @Version 1.0
 **/
public class RpcProxy {

    public static  <T> T create(Class clazz){
        MethodProxy methodProxy=new MethodProxy(clazz);
        T result= (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},methodProxy);
        return result;
    }
   static class MethodProxy implements InvocationHandler{

        private Class clazz;

        public MethodProxy(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(Object.class.equals(proxy.getClass().getDeclaredClasses())){
                return   method.invoke(this,args);
            }else {
                return rpcInvoke(proxy,method,args);
            }

        }

        private Object rpcInvoke(Object proxy, Method method, Object[] args) {
            InvokerMsg msg=new InvokerMsg();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setParams(method.getParameterTypes());
            msg.setValues(args);

            final RpcProxyHandler handler=new RpcProxyHandler();

            EventLoopGroup group=new NioEventLoopGroup();
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline=ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast("encoder",new ObjectEncoder())
                                    .addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(handler);

                        }
                    });
            try {
                ChannelFuture future= bootstrap.connect("127.0.0.1",9630).sync();
                future.channel().writeAndFlush(msg).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                group.shutdownGracefully();
            }
            return handler.getResponse();
        }
    }

}
