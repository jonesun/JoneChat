package com.jone.chat.logic;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.jone.chat.Constant;
import com.jone.chat.application.App;
import com.jone.chat.bean.User;
import com.jone.chat.enums.MessageType;
import com.jone.chat.ui.activity.ImageShowerActivity;

/**
 * Created by jone on 2014/6/25.
 */
public class ChatLogic {

    /**
     * 发送消息
     * @param toUser
     * @param messageType
     * @param content
     * @throws RemoteException
     */
    public void sendMsg(User toUser, MessageType messageType, String content) throws RemoteException {
        String messageTypeName = messageType.name();
        if(messageTypeName.equals(MessageType.PHOTO.name())){
            App.getInstance().getCoreService().sendPhoto(toUser, content);
        }else if(messageTypeName.equals(MessageType.VOICE.name())){
            App.getInstance().getCoreService().sendVoice(toUser, content);
        }else {
            App.getInstance().getCoreService().send(toUser, content);
        }
    }

    /**
     * 查看大图
     * @param context
     * @param imagePath
     * @param isNetworkPhoto
     */
    public void showLargeImage(Context context, String imagePath, boolean isNetworkPhoto){
        Intent intent = new Intent(context, ImageShowerActivity.class);
        intent.putExtra(Constant.IMAGE_PATH_KEY, imagePath);
        intent.putExtra("isNetworkPhoto", isNetworkPhoto);
        context.startActivity(intent);
    }

    public void showLargeImage(Context context, String imagePath){
        Intent intent = new Intent(context, ImageShowerActivity.class);
        intent.putExtra(Constant.IMAGE_PATH_KEY, imagePath);
        intent.putExtra("isNetworkPhoto", false);
        context.startActivity(intent);
    }
}
