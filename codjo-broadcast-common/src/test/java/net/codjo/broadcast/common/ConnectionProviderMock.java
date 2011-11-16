/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.test.common.mock.ConnectionMock;
import java.sql.Connection;
/**
 *
 */
public class ConnectionProviderMock implements ConnectionProvider {
    public Connection getConnection() {
        return new ConnectionMock();
    }


    public void releaseConnection(Connection connection) {
    }
}
