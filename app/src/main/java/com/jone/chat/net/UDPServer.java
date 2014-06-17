package com.jone.chat.net;

import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by jone on 2014/6/11.
 */
public class UDPServer implements UDPOperator{
    public static final int SOCKET_UDP_PORT = 8585;
    EventLoopGroup group;
    Bootstrap bootstrap;
    private Channel channel;

    public UDPServer start(UDPListener listener) {
        return start(SOCKET_UDP_PORT, listener);
    }

    public UDPServer start(int port, UDPListener listener) {
        try {
            if(!isActive()){
                group = new NioEventLoopGroup();
                bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new UDPHandler(listener));
                channel = bootstrap.bind(port).sync().channel();
                channel.closeFuture().await();
            }

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
        }finally {
            if(group != null){
                group.shutdownGracefully();
            }
            return this;
        }
    }

    @Override
    public boolean isActive() {
        if(channel != null && channel.isActive()){
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        if (isActive()) {
            channel.close();
            channel = null;
            if(group != null){
                group.shutdownGracefully();
            }
        } else {
            System.out.println("服务端未启动");
        }
    }

    public static void main(String[] args) {
        final UDPServer udpServer = new UDPServer();
        udpServer.start(new UDPListener() {
            @Override
            public void onOpened() {
                System.out.println("服务端开启");
            }

            @Override
            public void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver) {
                System.out.println("收到来自 " + remoteAddress + " 的消息: " + msg);
                receiver.receive("这是服务端,收到你的消息了。");
            }

            @Override
            public void onClosed() {
                System.out.println("服务端关闭");
            }
        });
    }
}
