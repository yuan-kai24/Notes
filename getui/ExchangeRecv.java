package com.wechat.aishangxuetang.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeRecv {
    private final static String QUEUE_NAME = "test_exchange_p";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.queueBind(QUEUE_NAME, "QUEUE_QXCHANGE_NAME_DIRECT","");

        // 同一时刻服务器只会发一条消息给消费者
        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(QUEUE_NAME, true/*手动返回消息*/, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            if (msg != null) {
                System.out.println("test_exchange_p收到消息： " + msg);
            }

            Thread.sleep(10);

//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
}
