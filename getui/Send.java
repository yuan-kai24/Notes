package com.wechat.aishangxuetang.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Send {
    private final static String QUEUE_NAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        sendEx();

    }

    private static void sendR() throws IOException, TimeoutException {
        long i = 0;

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        while (true) {
            String msg = ("随机发送消息：" + ++i);
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("send： " + msg);
        }

//        channel.close();
//        connection.close();
    }

    private static void sendEx() throws IOException, TimeoutException {
        long i = 0;

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare("QUEUE_QXCHANGE_NAME_DIRECT", "direct"/*fanout：订阅，direct：路由*/);

        String msg = "";
        Scanner scanner = new Scanner(System.in);
        msg = scanner.nextLine();
        channel.basicPublish("QUEUE_QXCHANGE_NAME_DIRECT", "2", null, msg.getBytes());
        channel.close();
        connection.close();
    }


}
