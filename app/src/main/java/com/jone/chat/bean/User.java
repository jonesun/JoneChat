package com.jone.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 用户类，对应局域网中每个在线用户的信息
 * Created by jone on 2014/6/17.
 */
public class User implements Parcelable{
    private String userName;	// 用户名
    private String ip;			//ip地址
    private String mac;			//MAC地址

    public User(){}

    public User(Parcel parcel){
        this.userName = parcel.readString();
        this.ip = parcel.readString();
        this.mac = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(ip);
        parcel.writeString(mac);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        /**
         * 将Parcel对象反序列化为ParcelableDate
         * 实现从source创建出JavaBean实例的功能
         * @param parcel
         * @return
         */
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}