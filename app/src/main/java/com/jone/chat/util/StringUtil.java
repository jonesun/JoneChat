package com.jone.chat.util;

import android.util.Base64;

/**
 * Created by jone on 2014/6/23.
 */
public class StringUtil {
    public static String encodeToStringByBase64(String str){
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP); //编码 解决字符串换行问题
    }

    public static String decodeByBase64(String encodeString){
        return new String(Base64.decode(encodeString.getBytes(), Base64.NO_WRAP));//解码
    }
}
