package org.example.Infrastructure.database;

import org.example.Configurable.URICollection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static Connection initConnection(String databaseUri) {
        try {
            return DriverManager.getConnection(databaseUri);
        } catch (SQLException e) {
            System.err.println("Connection failed... " + e.getMessage());
            throw new RuntimeException(e);
        }
    } // initConnection()

    public static boolean isValidConnection(Connection conn) {
        try {
            if (conn != null && conn.isValid(1000)) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    } // isValidConnection()

} // ConnectionFactory
