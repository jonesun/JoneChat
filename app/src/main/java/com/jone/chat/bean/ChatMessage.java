package com.jone.chat.bean;

import com.jone.chat.enums.MessageType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息类
 * Created by jone on 2014/6/17.
 */
public class ChatMessage implements Serializable{
    private String fromUserName;
    private MessageType messageType;
    private String content;
    private long sendTime;

    public ChatMessage(){}

    public ChatMessage(String fromUserName, String content){
        this.fromUserName = fromUserName;
        this.messageType = MessageType.TEXT;
        this.content = content;
        this.sendTime = System.currentTimeMillis();
    }

    public ChatMessage(String fromUserName, MessageType messageType, String content){
        this.fromUserName = fromUserName;
        this.messageType = messageType;
        this.content = content;
        this.sendTime = System.currentTimeMillis();
    }


    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
}
