package com.spring.netty.springnetty.netty.rpc.register;

import com.spring.netty.springnetty.netty.rpc.msg.InvokerMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName RegisterHandler
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 15:14
 * @Version 1.0
 **/
public class RegisterHandler extends ChannelInboundHandlerAdapter {

    private static ConcurrentHashMap<String,Object> registerMap=new ConcurrentHashMap<>();

    private  List<String> classCache=new ArrayList<>();

    public RegisterHandler() {
        //扫描包
        doScannerPackage("com.spring.netty.springnetty.netty.rpc.provider");
        //注册到map中
        doRegister();
    }

    public void doScannerPackage(String packageName){
        URL url= this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.","/"));
        File dir=new File(url.getFile().replaceAll("%20"," "));
        for (File file:dir.listFiles()){
            if(file.isDirectory()){
                doScannerPackage(packageName+"."+file.getName());
            }else{
                classCache.add(packageName+"."+file.getName().replaceAll(".class","").trim());
            }
        }
    }

    public void doRegister(){
        if (CollectionUtils.isEmpty(classCache))return;
        classCache.stream().forEach(e->{
            try {
                Class<?> clazz=Class.forName(e);
                Class<?> interfaces=clazz.getInterfaces()[0];
                registerMap.put(interfaces.getName(),clazz.newInstance());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("启动连接。。。。。");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result=new Object();
        InvokerMsg request=(InvokerMsg)msg;
        if(registerMap.containsKey(request.getClassName())){
            Object clazz=registerMap.get(request.getClassName());
            Method method=clazz.getClass().getMethod(request.getMethodName(),request.getParams());
            result=method.invoke(clazz,request.getValues());
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
      cause.printStackTrace();
    }
}
