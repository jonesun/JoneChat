package com.jone.chat.application;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.jone.chat.service.ICoreService;
import com.jone.chat.service.JettyService;
import com.jone.chat.util.FileUtils;
import com.jone.chat.util.SystemUtil;

import java.io.File;

/**
 * Created by jone on 2014/6/17.
 */
public class App extends Application {
    private static App instance;
    private static Serializer serializer;
    private ICoreService coreService;
    private static Handler handler;

    // 图片在SD卡中的缓存路径
    private static String IMAGE_PATH;

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

    public static Handler getHandler() {
        return handler;
    }

    public static String getIMAGE_PATH() {
        return IMAGE_PATH;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        serializer = new YamlSerializer();
        handler = new Handler();
        IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "Images" + File.separator;
        if(SystemUtil.getCurrentProcessName(this).equals(getPackageName())){
            Intent intent = new Intent("com.jone.chat.CoreService");
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("JettyService 启动...");
                        JettyService.start();
                    } catch (Exception e) {
                        System.out.println("JettyService 启动失败" + e.getMessage());
                    }
                }
            }).start();
        }
    }

//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        if(SystemUtil.getCurrentProcessName(this).equals(getPackageName())){
//            unbindService(serviceConnection);
//        }
//    }

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
