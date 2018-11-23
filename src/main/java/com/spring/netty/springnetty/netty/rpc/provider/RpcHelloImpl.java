package com.spring.netty.springnetty.netty.rpc.provider;

import com.spring.netty.springnetty.netty.rpc.api.IRpcHello;

/**
 * @ClassName RpcHelloImpl
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 14:52
 * @Version 1.0
 **/
public class RpcHelloImpl implements IRpcHello {
    @Override
    public String sayHello(String name) {

        return "Hello "+name;
    }
}
