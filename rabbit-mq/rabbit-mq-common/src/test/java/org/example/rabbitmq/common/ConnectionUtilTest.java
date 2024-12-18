package org.example.rabbitmq.common;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionUtilTest {

    @Test
    void getFactory() {
        ConnectionFactory factory = ConnectionUtil.getFactory();
        assertNotNull(factory);
    }

    @Test
    void getConnection() throws Exception {
        Connection connection = null;
        try {
            connection = ConnectionUtil.getConnection();
            assertNotNull(connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}