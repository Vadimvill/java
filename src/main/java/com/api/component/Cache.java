package com.api.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {
    private Map<String,Object> hashMap;

    public Cache() {
        this.hashMap = new HashMap<>();
    }
    public Object get(String key){
        return hashMap.get(key);
    }
    public void put(String key,Object obj){
        hashMap.put(key,obj);
    }
    public void remove(String key){
        hashMap.remove(key);
    }
    public void clear(){
        hashMap.clear();
    }

}
