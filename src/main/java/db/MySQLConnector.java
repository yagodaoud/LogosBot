package main.java.db;

import java.sql.*;
public class MySQLConnector {

    public static Connection getConnection() throws SQLException{
        final String dbConnection = "jdbc:mysql://localhost:3306/discordbot";
        final String dbUser = "root";
        final String dbPassword = "1234";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
            System.out.println("Successfully connected to the database");
        } catch (SQLException e) {
            System.out.println("Connection failed");
        }

        return connection;
    }
}
