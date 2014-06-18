package com.jone.chat.net;


/**
 * Created by jone on 2014/6/12.
 */
public abstract class UDPClientOperator implements UDPOperator {
    public void sendMsg(String ip, String msg){
        sendMsg(ip, UDPServer.SOCKET_UDP_PORT, msg);
    }
    public abstract void sendMsg(String ip, int port, String msg);
}
