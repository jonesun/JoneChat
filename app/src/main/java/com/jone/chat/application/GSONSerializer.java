package com.jone.chat.application;

import com.google.gson.Gson;

/**
 * Created by jone on 2014/6/18.
 */
public class GSONSerializer implements Serializer {
    private Gson gson;
    public GSONSerializer(){
        gson = new Gson();
    }
    @Override
    public String dump(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    @Deprecated
    public Object load(String str) {
        return null;
    }

    @Override
    public <T> T loadAs(String str, Class<T> clazz) throws Exception {
        return gson.fromJson(str, clazz);
    }
}
