package com.jone.chat.application;

import org.yaml.snakeyaml.Yaml;

/**
 * Created by jone on 2014/6/11.
 */
public class YamlSerializer implements Serializer {
    private static Yaml yaml = new Yaml();

    @Override
    public String dump(Object obj){
        return yaml.dump(obj);
    }

    @Override
    public Object load(String str){
        return yaml.load(str);
    }

    @Override
    public <T> T loadAs(String str, Class<T> clazz) throws Exception{
        return yaml.loadAs(str, clazz);
    }
}
