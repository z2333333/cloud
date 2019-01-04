package com.cloud.controller;

import com.cloud.common.Const;
import com.cloud.dao.mapper.TestMapper;
import com.cloud.dao.mapper.UserMapper;
import com.cloud.dao.model.User;
import com.cloud.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created by zhengxin on 2018/7/18.
 */
@RestController
public class TestController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TestMapper testMapper;

    @RequestMapping("/hello")
    public String helloDB() {

        return "hello";
    }

    @RequestMapping("threadTest")
    public String threadTest() {
        /*new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                try {
                    System.out.println("线程:"+Thread.currentThread().getName()+"正在等待...");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}
        }.run();*/
        try {
            System.out.println("线程:"+Thread.currentThread().getName()+"正在睡眠...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/insert")
    public String hello() {
        int num = 0;
        for (int i = 0; i < 600000; i++) {
            User user = new User();
            user.setRole(Const.Role.ROLE_CUSTOMER);
            user.setPassword(MD5Util.MD5EncodeUtf8("123"));
            user.setUsername(createRandom(new Random().nextInt(6) + 6));
            user.setEmail(createRandom(8) + "@.qq.com");
            user.setPhone(createRandomPhone(11));
            user.setQuestion(createRandom(new Random().nextInt(3) + 3));
            user.setAnswer(createRandom(new Random().nextInt(4) + 3));
            try {
                userMapper.insert(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println((++num / 1000000.0) * 100);
        }

        return "success!";
    }

    public static String createRandom(int len) {
        String base = "abcdefghijklnmopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int random = new Random().nextInt(26);
            if (i == 0) {
                result.append((char) (base.charAt(random) - 32));
            } else {
                result.append(base.charAt(random));
            }
        }
        return result.toString();
    }

    public static String createRandomPhone(int len) {
        String base = "123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int random = new Random().nextInt(8);
            result.append(base.charAt(random));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(createRandomPhone(11));
    }
}
