package com.jone.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.jone.chat.Constant;
import com.jone.chat.application.App;
import com.jone.chat.bean.CommunicationBean;
import com.jone.chat.bean.User;
import com.jone.chat.net.MethodArgsException;
import com.jone.chat.net.UDPClient;
import com.jone.chat.net.UDPListener;
import com.jone.chat.net.UDPReceiver;
import com.jone.chat.net.UDPServer;
import com.jone.chat.util.SystemUtil;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class CoreService extends Service {
    private Map<String, User> userMap = new HashMap<>();
    private UDPServer udpServer;
    private UDPClient udpClient;
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("CoreService onCreate");
        startUDPServer();
        startUDPClient();
    }

    private void startUDPServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                udpServer = new UDPServer().start(new UDPListener() {
                    @Override
                    public void onOpened() {
                        System.out.println("udpServer onOpened");
                    }

                    @Override
                    public void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver) {
                        try {
                            CommunicationBean communicationBean = App.getSerializer().loadAs(msg, CommunicationBean.class);
                            String action = communicationBean.getAction();
                            Object data = communicationBean.getData();
                            switch (action){
                                case Constant.USER_ONLINE_ACTION:
                                    if(data != null){
                                        User user = (User) data;
                                        System.out.println("user: " + user.getUserName());
                                        saveUser(user);
                                        receiver.receive(App.getSerializer().dump(getOnlineInfo())); //告知自己在线
                                    }
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        Looper.prepare();
//                        Toast.makeText(CoreService.this, "udpServer onReceived " + remoteAddress + " msg: " + msg , Toast.LENGTH_LONG).show();
//                        Looper.loop();
                    }

                    @Override
                    public void onClosed() {
                        System.out.println("udpServer onClosed");
                    }
                });
            }
        }).start();
    }

    private void startUDPClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    udpClient = new UDPClient().start(new UDPListener() {
                        @Override
                        public void onOpened() {
                            System.out.println("udpClient onOpened");
                            udpClient.sendMsg("255.255.255.255", App.getSerializer().dump(getOnlineInfo()));
                        }

                        @Override
                        public void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver) {
                            try {
                                CommunicationBean communicationBean = App.getSerializer().loadAs(msg, CommunicationBean.class);
                                String action = communicationBean.getAction();
                                Object data = communicationBean.getData();
                                switch (action){
                                    case Constant.USER_ONLINE_ACTION:
                                        if(data != null){
                                            User user = (User) data;
                                            System.out.println("user: " + user.getUserName());
                                            saveUser(user);
                                        }
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClosed() {
                            System.out.println("udpClient onClosed");
                        }
                    });
                } catch (InterruptedException | SocketException | MethodArgsException e) {
                    udpClient.close();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public CommunicationBean getOnlineInfo(){
        User localUser = getLocalUser();
        return new CommunicationBean(localUser.getIp(), null, Constant.USER_ONLINE_ACTION, localUser);
    }

    private User getLocalUser(){
        String ip = SystemUtil.getLocalIpAddress();
        User user = new User();
        user.setUserName("用户(" + ip + ")");
        user.setIp(ip);
        user.setMac(SystemUtil.getLocalMacAddress(CoreService.this));
        return user;
    }

    private void saveUser(User user){
        userMap.put(user.getIp(), user);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return stub;
    }

    ICoreService.Stub stub = new ICoreService.Stub(){

        @Override
        public Map getOnlineUsers() throws RemoteException {
            return userMap;
        }

        @Override
        public void send(String msg) throws RemoteException {
            System.out.println("send: " + msg);
//            if(udpClient != null){
//                udpClient.sendMsg("255.255.255.255", msg);
//            }
        }

        @Override
        public String receive() throws RemoteException {
            System.out.println("receive");
            return "receive";
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(udpServer != null){
            udpServer.close();
        }
        if(udpClient != null){
            udpClient.close();
        }
    }
}
