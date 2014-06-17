package com.jone.chat.net;

import home.common.configs.constants.Constants;

/**
 * Created by jone on 2014/6/12.
 */
public abstract class UDPClientOperator implements UDPOperator {
    void sendMsg(String ip, String msg){
        sendMsg(ip, Constants.SOCKET_UDP_PORT, msg);
    }
    abstract void sendMsg(String ip, int port, String msg);
}
