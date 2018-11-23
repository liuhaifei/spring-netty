package com.spring.netty.springnetty.netty.rpc.msg;

import java.io.Serializable;

/**
 * @ClassName InvokerMsg
 * @Description TODO
 * @Author 刘海飞
 * @Date 2018/11/23 16:30
 * @Version 1.0
 **/
public class InvokerMsg implements Serializable{

    private String className;
    private String methodName;
    private Class<?>[] params;
    private Object[] values;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
