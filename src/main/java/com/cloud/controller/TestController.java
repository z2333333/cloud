package com.cloud.controller;

import com.cloud.common.Const;
import com.cloud.common.ResponseCode;
import com.cloud.common.ServerResponse;
import com.cloud.dao.mapper.TestMapper;
import com.cloud.dao.mapper.UserMapper;
import com.cloud.dao.model.User;
import com.cloud.util.CookieUtil;
import com.cloud.util.JsonUtil;
import com.cloud.util.MD5Util;
import com.cloud.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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
        try {
            return "hello:"+getLocalIP();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地IP地址
     *
     * @throws SocketException
     */
    public static String getLocalIP() throws UnknownHostException {
        if (isWindowsOS()) {
            return InetAddress.getLocalHost().getHostAddress();
        } else {
            return getLinuxLocalIp();
        }
    }

    /**
     * 判断操作系统是否是Windows
     *
     * @return
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    private static String getLinuxLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                                System.out.println(ipaddress);
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("获取ip地址异常");
            ip = "unknown";
            ex.printStackTrace();
        }
//        System.out.println("IP:"+ip);
        return ip;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @RequestMapping("/sessionTest")
    public ServerResponse<Object> sessionTest(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return null;
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
