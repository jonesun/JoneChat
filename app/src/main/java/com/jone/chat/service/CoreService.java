package com.jone.chat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.RemoteViews;

import com.jone.chat.Constant;
import com.jone.chat.R;
import com.jone.chat.application.App;
import com.jone.chat.bean.CommunicationBean;
import com.jone.chat.bean.User;
import com.jone.chat.net.MethodArgsException;
import com.jone.chat.net.UDPClient;
import com.jone.chat.net.UDPListener;
import com.jone.chat.net.UDPReceiver;
import com.jone.chat.net.UDPServer;
import com.jone.chat.ui.activity.ChatRoomActivity;
import com.jone.chat.util.SystemUtil;

import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CoreService extends Service {
    private Map<String, User> userMap = new HashMap<>();
    private UDPServer udpServer;
    private UDPClient udpClient;
    private boolean isUIDestroy;
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("CoreService onCreate");
        online();
    }
    private void online(){
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
                        System.out.println("udpServer onReceived " + remoteAddress);
                        try {
                            CommunicationBean communicationBean = App.getSerializer().loadAs(msg, CommunicationBean.class);
                            String action = communicationBean.getAction();
                            Object data = communicationBean.getData();
                            switch (action){
                                case Constant.NET_USER_ONLINE_ACTION:
                                    User user = (User) data;
                                    user.setLastActiveTime(System.currentTimeMillis());
                                    userMap.put(user.getIp(), user);
                                    receiver.receive(App.getSerializer().dump(getLocalOnlineInfo()));
                                    break;
                                case Constant.NET_SEND_MSG:
                                    String receiveMsg = data.toString();
                                    System.out.println("receiveMsg: " + receiveMsg);
                                    if(isUIDestroy){ //如果UI关闭了,则发一个通知
                                        showNotification(communicationBean.getFromUser(), receiveMsg);
                                    }else {
                                        Intent intent = new Intent();
                                        intent.putExtra("fromUser", communicationBean.getFromUser());
                                        intent.putExtra("receiveMsg", receiveMsg);
                                        intent.setAction(Constant.BROADCAST_RECEIVE_MSG_ACTION);
                                        sendBroadcast(intent);
                                    }

                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        }

                        @Override
                        public void onReceived(SocketAddress remoteAddress, String msg, UDPReceiver receiver) {
                            System.out.println("udpClient onReceived " + remoteAddress);
                            try {
                                CommunicationBean communicationBean = App.getSerializer().loadAs(msg, CommunicationBean.class);
                                String action = communicationBean.getAction();
                                Object data = communicationBean.getData();
                                switch (action){
                                    case Constant.NET_USER_ONLINE_ACTION:
                                        if(data != null){
                                            User user = (User) data;
                                            user.setLastActiveTime(System.currentTimeMillis());
                                            if(user.getIp().equals(SystemUtil.getLocalIpAddress())){
                                                return;
                                            }
                                            userMap.put(user.getIp(), user);
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

    public CommunicationBean getLocalOnlineInfo(){
        User localUser = getLocalUser();
        return new CommunicationBean(localUser, null, Constant.NET_USER_ONLINE_ACTION, localUser);
    }

    /**
     * 在状态栏显示通知
     */
    private void showNotification(User fromUser, String receiveMsg){
        NotificationManager notificationManager = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击notification之后，该notification自动消失
        Intent notificationIntent = new Intent(CoreService.this, ChatRoomActivity.class); // 点击该通知后要跳转的Activity
        notificationIntent.putExtra("fromUser", fromUser);
        notificationIntent.putExtra("receiveMsg", receiveMsg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.txtTile, fromUser.getUserName() + "发来消息");
        remoteViews.setTextViewText(R.id.txtContent, receiveMsg);
        notification.contentView = remoteViews;
        notification.contentIntent = contentIntent;
        notificationManager.notify(0, notification);
    }


    private User getLocalUser(){
        String localIP = SystemUtil.getLocalIpAddress();
        User localUser = new User();
        localUser.setUserName("用户(" + localIP + ")");
        localUser.setIp(localIP);
        localUser.setMac(SystemUtil.getLocalMacAddress(CoreService.this));
        return localUser;
    }


    private List<User> getOnlineUserList(){
        List<User> onlineUsers = new ArrayList<>();
        if(userMap != null || userMap.size() > 0){
            long nowTime = System.currentTimeMillis();
            for(User user : userMap.values()){
                if(nowTime - user.getLastActiveTime() <= (1000 * 60)){ //如果用户1分钟之内有返回自己的状态则认为该用户在线
                    onlineUsers.add(user);
                }
            }
        }
        return onlineUsers;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("CoreService onUnbind");
        return super.onUnbind(intent);
    }

    ICoreService.Stub stub = new ICoreService.Stub(){

        @Override
        public void send(User toUser, String msg) throws RemoteException {
            CommunicationBean communicationBean = new CommunicationBean(getLocalUser(), toUser, Constant.NET_SEND_MSG, msg);
            udpClient.sendMsg(toUser.getIp(), App.getSerializer().dump(communicationBean));
        }

        @Override
        public List<User> getOnlineUsers() throws RemoteException {
            return getOnlineUserList();
        }

        @Override
        public void noticeOnline() throws RemoteException {
            udpClient.sendMsg("255.255.255.255", App.getSerializer().dump(getLocalOnlineInfo()));
        }

        @Override
        public void noticeUIState(boolean isDestroy) throws RemoteException {
            isUIDestroy = isDestroy;
            System.out.println("UI界面isDestroy: " + isDestroy);
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
        System.out.println("CoreService onDestroy");
    }
}
