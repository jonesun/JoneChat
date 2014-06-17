package com.jone.chat.net;

import java.net.SocketAddress;

/**
 * Created by jone on 2014/6/12.
 */
public interface UDPListener {
    void onOpened();
    void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver);
    void onClosed();
}
