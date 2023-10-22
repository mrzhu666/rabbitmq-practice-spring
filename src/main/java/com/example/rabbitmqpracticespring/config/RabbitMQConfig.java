package com.example.rabbitmqpracticespring.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String queueAName = "queueA";
    public static final String queueBName = "queueB";
    public static final String topicExchangeName = "spring.direct";
    public static final String dlQueueName = "deadLetter";
    public static final String dlExchangeName = "dl.direct";
    
    /**
     * 构建队列时绑定死信交换器
     */
    @Bean
    public Queue queueA() {
        return QueueBuilder
            // 非持久化，指定队列名称
            .nonDurable(queueAName)
            //指定死信交换机
            .deadLetterExchange(dlExchangeName)
            //指定死信路由键
            .deadLetterRoutingKey("dl_key")
            //最大长度，这个数值方便测试
            .maxLength(3)
            //超时时间，毫秒
            .ttl(5000)
            .build();
    }
    
    /**
     * 构建队列时绑定死信交换器
     */
    @Bean
    public Queue queueB() {
        return QueueBuilder
            // 非持久化，指定队列名称
            .nonDurable(queueBName)
            //指定死信交换机
            .deadLetterExchange(dlExchangeName)
            //指定死信路由键
            .deadLetterRoutingKey("dl_key")
            //最大长度，这个数值方便测试
            .maxLength(3)
            //超时时间，毫秒
            .ttl(5000)
            .build();
    }
    
    /**
     * 定义topic类型交换器
     */
    @Bean
    public TopicExchange exchange() {
        return ExchangeBuilder
            .topicExchange(topicExchangeName)
            .durable(false)
            .build();
    }
    
    /**
     * 绑定路由器和队列
     */
    @Bean
    public Binding bindingA(@Qualifier(queueAName) Queue queue, TopicExchange exchange) {
        return BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("foo.bar.#");
    }
    
    /**
     * 绑定路由器和队列
     */
    @Bean
    public Binding bindingB(@Qualifier(queueBName) Queue queue, TopicExchange exchange) {
        return BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("foo.bar.*");
    }
    
    /**
     * 构建死信队列
     */
    @Bean(dlQueueName)  // spring容器里组件的名字
    public Queue dlQueue() {
        return QueueBuilder
            .nonDurable(dlQueueName) // rabbitmq里队列名字
            .build();
    }
    
    /**
     * 构建死信交换机
     */
    @Bean(dlExchangeName)  // spring容器里组件的名字
    public Exchange dlExchange() {
        return ExchangeBuilder
            .directExchange(dlExchangeName) // rabbitmq里交换机名字
            .durable(false)
            .build();
    }
    
    /**
     * 绑定死信交换机和死信队列
     */
    @Bean
    public Binding dlBinding(@Qualifier(dlExchangeName) Exchange exchange,
                             @Qualifier(dlQueueName) Queue queue) {
        return BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("dl_key")
            .noargs();
    }
}
