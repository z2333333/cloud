package com.cloud.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengxin on 2017/10/17.
 */
public final class HeadAndParamsMap{

    private Map<String,String> params = new HashMap<>();
    private Map<String,String> headers = new HashMap<>();

//    public HeadAndParamsMap(){
//        super();
//    }

    public HeadAndParamsMap setHeadeas(String key, String value){
        if(key != null && !("".equals(key))){
            headers.put(key,value);
        }
        return this;
    }

    public HeadAndParamsMap setParams(String key, String value){
        if(key != null && !("".equals(key))){
            params.put(key,value);
        }
        return this;
    }

    public void clear(boolean all){
        if(all){
           params.clear();
           headers.clear();
        }else {
           params.clear();
        }
    }

    public Map<String,String> getHeadeas(){
        return headers;
    }

    public Map<String,String> getParams(){
        return params;
    }
}
