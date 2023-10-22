package com.example.rabbitmqpracticespring.message;

import com.example.rabbitmqpracticespring.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
public class Receiver {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 监听队列
     */
    @RabbitListener(queues = {RabbitMQConfig.queueAName})
    public void receiveAMessage(String messageStr, Channel channel, Message message) throws IOException {
        // 拿到消息延迟消费
        try {
            //System.out.println("Received A <" + messageStr + ">");
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
            Thread.sleep(400);
            //return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取消息id，存储到redis，防止重复消费
        String messageID = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        try {
            // 保存消息id到redis，缓存时间为10秒
            if (BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(messageID, "0", 10, TimeUnit.SECONDS))) {
                //消费消息
                System.out.println("Received A <" + messageStr + ">");
                //将value设置为1
                redisTemplate.opsForValue().set(messageID, "1", 10, TimeUnit.SECONDS);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                // 拒绝消息，且不返回消息到队列，将变为死信。如果消费失败需要手动删除该键？
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // 判断是否消费过。如果判断出0，说明被其它消费者消费？会不会和消费失败冲突？
                if (StringUtils.equalsIgnoreCase("1", (String)redisTemplate.opsForValue().get(messageID))) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                }
            }
            
        } catch (Exception e) {
            // 发生异常，说明消费失败
            // 删除消息ID
            redisTemplate.delete(messageID);
            // 拒绝确认消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            System.err.println("get msg1 failed msg = " + messageStr);
        }
    }
    /**
     * 监听队列
     */
    @RabbitListener(queues = {RabbitMQConfig.queueBName})
    public void receiveBMessage(String messageStr, Channel channel, Message message) throws IOException {
        // 拿到消息延迟消费
        try {
            //System.out.println("Received B <" + messageStr + ">");
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
            Thread.sleep(400);
            //return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取消息id，存储到redis，防止重复消费
        String messageID = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        try {
            // 保存消息id到redis，缓存时间为10秒
            if (BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(messageID, "0", 10, TimeUnit.SECONDS))) {
                //消费消息
                System.out.println("Received B <" + messageStr + ">");
                //将value设置为1
                redisTemplate.opsForValue().set(messageID, "1", 10, TimeUnit.SECONDS);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                // 拒绝消息，且不返回消息到队列，将变为死信。如果消费失败需要手动删除该键？
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // 判断是否消费过。如果判断出0，说明被其它消费者消费？会不会和消费失败冲突？
                if (StringUtils.equalsIgnoreCase("1", (String)redisTemplate.opsForValue().get(messageID))) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                }
            }
            
        } catch (Exception e) {
            // 发生异常，说明消费失败
            // 删除消息ID
            redisTemplate.delete(messageID);
            // 拒绝确认消息，且重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            System.err.println("get msg1 failed msg = " + messageStr);
        }
    }
    
    /**
     * 死信队列监听
     */
    @RabbitListener(queues = RabbitMQConfig.dlQueueName)
    public void receiverDl(String messageStr, Channel channel, Message message) throws IOException {
        try {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("Dead Letter <" + messageStr + ">");
        } catch (Exception e) {
            // 拒绝确认消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            System.err.println("get msg1 failed msg = " + messageStr);
        }
    }
}
