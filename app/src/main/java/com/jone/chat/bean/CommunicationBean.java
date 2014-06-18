package com.jone.chat.bean;

import java.io.Serializable;

/**
 * Created by jone on 2014/6/18.
 */
public class CommunicationBean implements Serializable{
    private String fromCode;
    private String toCode;
    private String action;
    private Object data;

    public CommunicationBean(){}

    public CommunicationBean(String fromCode, String toCode, String action, Object data){
        this.setFromCode(fromCode);
        this.setToCode(toCode);
        this.setAction(action);
        this.setData(data);
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
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
