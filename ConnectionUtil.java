package com.wechat.aishangxuetang.test;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtil {
    private final static  String ip = "47.100.225.254";
    private final static  int port = 5672;
    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ip);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost("/yuankai");
        connectionFactory.setUsername("yuankai");
        connectionFactory.setPassword("kai1024");
        return connectionFactory.newConnection();

    }
}
