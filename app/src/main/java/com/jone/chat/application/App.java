package com.jone.chat.application;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.jone.chat.service.ICoreService;
import com.jone.chat.util.SystemUtil;

/**
 * Created by jone on 2014/6/17.
 */
public class App extends Application {
    private static App instance;
    private static Serializer serializer;
    private ICoreService coreService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            coreService = ICoreService.Stub.asInterface(iBinder);
            System.out.println("serviceConnection onServiceConnected");
//            try {
//                getCoreService().send("wwwww");
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            coreService = null;
            System.out.println("serviceConnection onServiceDisconnected");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        serializer = new YamlSerializer();
        if(SystemUtil.getCurrentProcessName(this).equals(getPackageName())){
            Intent intent = new Intent("com.jone.chat.CoreService");
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public ICoreService getCoreService() {
        return coreService;
    }

    public static Serializer getSerializer() {
        return serializer;
    }
}