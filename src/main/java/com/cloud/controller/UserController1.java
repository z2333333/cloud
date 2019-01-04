package com.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhengxin on 2018/7/19.
 */
@RestController
@RequestMapping("/user")
public class UserController1 {

    /*@Autowired
    UserMapper userMapper;

    @RequestMapping("/exist")
    public boolean isExist(String name){
        if (name==null) {
            name = "";
        }
        User user = new User();
        user.setName(name);
        List<User> users = userMapper.select(user);
        if (users.size()>0) {
            System.out.println("***********该用户已存在*********:"+users.get(0).getName());
            return true;
        }
        System.out.println("未命中:"+name);
        return false;
    }*/
}
