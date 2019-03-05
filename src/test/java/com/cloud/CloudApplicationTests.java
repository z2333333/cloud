package com.cloud;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.jms.Destination;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudApplicationTests {

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Test
    public void contextLoads() {
        Destination destination = new ActiveMQQueue("test.queue");
        long cur_time = System.currentTimeMillis();
        for (int i = 0; i <= 10000; i++) {
            send(destination);
        }
        System.out.println(System.currentTimeMillis()-cur_time);
    }

    @Async
    public void send(Destination destination){
        jmsMessagingTemplate.convertAndSend(destination, 1);
    }
}
