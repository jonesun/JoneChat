package com.jone.chat.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by jone on 2014/6/17.
 */
public class SystemUtil {
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    //判断wifi是否打开
    public static boolean isWifiActive(Context context){
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(mConnectivity != null){
            NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();

            if(infos != null){
                for(NetworkInfo ni: infos){
                    if("WIFI".equals(ni.getTypeName()) && ni.isConnected())
                        return true;
                }
            }
        }

        return false;
    }

    // 获取本机IP地址
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress.getClass().getName().equals("java.net.Inet4Address")) {
                            String ip = inetAddress.getHostAddress().toString();
                            if (ip.contains("192.168"))  //对于多网卡或外网， 根据实际情况修改
                                return ip;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

//    //得到本机IP地址
//    public static String getLocalIpAddress(){
//        try{
//            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
//            while(en.hasMoreElements()){
//                NetworkInterface nif = en.nextElement();
//                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
//                while(enumIpAddr.hasMoreElements()){
//                    InetAddress mInetAddress = enumIpAddr.nextElement();
//                    if(!mInetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(mInetAddress.getHostAddress())){
//                        return mInetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        }catch(SocketException ex){
//            Log.e("MyFeiGeActivity", "获取本地IP地址失败");
//        }
//
//        return "127.0.0.1";
//    }

    //获取本机MAC地址
    public static String getLocalMacAddress(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
}
