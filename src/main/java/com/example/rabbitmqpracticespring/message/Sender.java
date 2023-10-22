package com.example.rabbitmqpracticespring.message;

import com.example.rabbitmqpracticespring.config.RabbitMQConfig;
import javax.annotation.Resource;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Sender implements CommandLineRunner {
    
    public static final String routingKey = "foo.bar.baz";
    @Resource
    public RabbitTemplate rabbitTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message..");
        for (int i = 0; i < 1; i++) {
            CorrelationData messageID = new CorrelationData(UUID.randomUUID().toString());  // 生成消息id
            rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, routingKey, "Message Hello " + i,messageID);
            Thread.sleep(10);
        }
    }
}
