package com.cloud.service;

import com.cloud.controller.HeadAndParamsMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by zhengxin on 2018/8/7.
 */
@Service
public class HttpCommonService {

    private RestTemplate restTemplates;

    public String executeRequest(final String url, HeadAndParamsMap headAndParamsMap, HttpMethod method){//通用的请求处理
        HttpHeaders httpHeader = null;
        if(restTemplates == null)
            restTemplates = new RestTemplate();
        if(headAndParamsMap.getHeadeas().size() > 0){
            httpHeader = new HttpHeaders();
            for(Map.Entry<String,String> h : headAndParamsMap.getHeadeas().entrySet()){
                httpHeader.add(h.getKey(),h.getValue());
            }
        }

        String result = null;
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, httpHeader);
        try{
            ResponseEntity<String> response = restTemplates.exchange(url, method, requestEntity, String.class,headAndParamsMap.getParams());//返回对象,自定义bean,api
            result = response.getBody();
//            System.out.println("接收数据："+result);
        }catch (RestClientException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return result;
    }
}
