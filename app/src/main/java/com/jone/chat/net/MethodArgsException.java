package com.jone.chat.net;

/**
 * Created by jone on 2014/6/10.
 * 方法参数传递异常
 */
public class MethodArgsException extends Exception {
    public MethodArgsException(){
        super();
    }
    public MethodArgsException(String msg){
        super(msg);
    }
}
