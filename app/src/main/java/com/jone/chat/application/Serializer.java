package com.jone.chat.application;

public interface Serializer {
    public String dump(Object obj);
    public Object load(String str);
    public <T> T loadAs(String str, Class<T> clazz) throws Exception;
}
