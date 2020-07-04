package com.wechat.aishangxuetang.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.sun.javafx.font.PrismFontStrike;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    private final static String QUEUE_NAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(QUEUE_NAME, true, consumer);
        
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            if(msg != null){
                System.out.println("收到消息：" + msg);
            }
        }
//        channel.close();
//        connection.close();
    }



}
