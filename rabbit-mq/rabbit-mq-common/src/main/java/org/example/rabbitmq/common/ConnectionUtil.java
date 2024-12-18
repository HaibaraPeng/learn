package org.example.rabbitmq.common;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtil {

    private static final String HOST = "localhost";
    private static final Integer PORT = 5672;
    private static final String VIRTUAL_HOST = "/";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "123456";

    public static ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        return factory;
    }

    public static Connection getConnection() throws Exception {
        return getFactory().newConnection();
    }
}
