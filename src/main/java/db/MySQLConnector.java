package main.java.db;

import java.sql.*;
public class MySQLConnector {

    public static Connection getConnection() throws SQLException {

        final String dbConnection = "jdbc:mysql://localhost";
        final String dbUser = "root";
        final String dbPassword = "1234";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
            System.out.println("Successfully connected to db");

        } catch (SQLException e) {
            System.out.println("Connection failed");
        } finally {
            if (connection != null){
                try {
                    connection.close();
                    System.out.println("Connection closed");
                } catch (SQLException e){
                    System.out.println("Failed to close connection");
                }
            }
        }
        return connection;
    }
}
