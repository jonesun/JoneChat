package com.jone.chat.net;

import com.jone.chat.util.StringUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * Created by jone on 2014/6/11.
 */
public class UDPClient extends UDPClientOperator{
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private UDPHandler udpHandler;
    private Channel channel;

    public UDPClient start(UDPListener listener) throws InterruptedException, SocketException, MethodArgsException {
        if(!isActive()){
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            udpHandler = new UDPHandler(listener);
            bootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(udpHandler);
            channel = bootstrap.bind(0).sync().channel();
//            if (!channel.closeFuture().await(5000)) {
//                System.err.println("QOTM request timed out.");
//            }
        }
        return this;
    }

    @Override
    public void sendMsg(String ip, int port, String msg){
        msg = StringUtil.encodeToStringByBase64(msg);//编码
        if(udpHandler != null){
            try {
                udpHandler.getChannelHandlerContext().writeAndFlush(
                        new DatagramPacket(
                                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                                new InetSocketAddress(ip, port))).await();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                //e.printStackTrace();
            }
        }else {
            System.out.println("未连接或连接已断开");
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
        if(isActive()){
            channel.disconnect();
            channel = null;
            if(eventLoopGroup != null){
                eventLoopGroup.shutdownGracefully();
            }
        }
    }


    private static UDPClient udpClient;
    public static void main(String[] args) {
        try {
            udpClient = new UDPClient();
            udpClient.start(new UDPListener() {
                @Override
                public void onOpened() {
                    System.out.println("客户端开启.");
                    while (true){
                        try {
                            Thread.sleep(1000);
                            udpClient.sendMsg("127.0.0.1", "你好, 这是客户端");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                    udpClient.close();
                }

                @Override
                public void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver) {
                    System.out.println("收到来自 " + remoteAddress + " 的消息: " + msg);
                    udpClient.close();
                }

                @Override
                public void onClosed() {
                    System.out.println("客户端关闭");
                }
            });

        } catch (InterruptedException | SocketException |MethodArgsException e) {
            System.out.println(e.getMessage());
            udpClient.close();
        }
    }
}
