package com.jone.chat.net;

import com.jone.chat.util.StringUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * Created by jone on 2014/6/11.
 */
public class UDPHandler extends SimpleChannelInboundHandler<DatagramPacket> implements UDPReceiver {
    private ChannelHandlerContext channelHandlerContext;
    private DatagramPacket datagramPacket;
    private UDPListener listener;
    public UDPHandler(UDPListener listener) {
        this.listener = listener;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        channelHandlerContext = ctx;
        datagramPacket = packet;
        if (listener != null) {
            String response = packet.content().toString(CharsetUtil.UTF_8);
            response = StringUtil.decodeByBase64(response); //解码
            listener.onReceived(packet.sender(), response, this);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelHandlerContext = ctx;
        if (listener != null) {
            listener.onOpened();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelHandlerContext = ctx;
        if (listener != null) {
            listener.onClosed();
        }
    }

    @Override
    public void receive(String msg){
        msg = StringUtil.encodeToStringByBase64(msg);//编码
        if(getChannelHandlerContext() != null && datagramPacket != null){
            getChannelHandlerContext().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8), datagramPacket.sender()));
        }
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
