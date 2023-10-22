package com.example.rabbitmqpracticespring;

import com.example.rabbitmqpracticespring.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RabbitmqPracticeSpringApplicationTests {
    
    @Resource
    private RabbitTemplate rabbitTemplate;
    
    @Test
    void producer() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName,"foo.bar.user","message User");
    }
    
}
