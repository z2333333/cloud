package com.cloud.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * Created by geely
 */
public class PropertiesUtil {


    private static Properties props;

    static {//PropertiesUtil.class.getClassLoader().getResource("").getPath()
        //ResourceUtils.getFile("classpath/mmall.properties").getPath()
        String fileName = "mmall.properties";
        props = new Properties();
        try {
//            File f = new File(PropertiesUtil.class.getResource("/").getPath());
//            String path = f.getAbsolutePath()+"\\"+fileName;
//            InputStream is= new FileInputStream(path);
//            props.load(is);
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }



}
