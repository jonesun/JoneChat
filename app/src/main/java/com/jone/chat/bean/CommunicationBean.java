package com.jone.chat.bean;

import java.io.Serializable;

/**
 * Created by jone on 2014/6/18.
 */
public class CommunicationBean implements Serializable{
    private User fromUser;
    private User toUser;
    private String action;
    private Object data;

    public CommunicationBean(){}

    public CommunicationBean(User fromUser, User toUser, String action, Object data){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.action = action;
        this.data = data;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
