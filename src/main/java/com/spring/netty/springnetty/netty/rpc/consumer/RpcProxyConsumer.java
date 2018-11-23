package com.spring.netty.springnetty.netty.rpc.consumer;

import com.spring.netty.springnetty.netty.rpc.api.IRpcHello;

/**
 * @ClassName RpcProxyConsumer
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 17:34
 * @Version 1.0
 **/
public class RpcProxyConsumer {

    public static void main(String[] args) {
        IRpcHello hello=RpcProxy.create(IRpcHello.class);

        System.out.println(hello.sayHello("lhf"));
    }
}
