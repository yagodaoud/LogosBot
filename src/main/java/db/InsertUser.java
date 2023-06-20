package main.java.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUser {

    public static void main(String[] args) throws SQLException {
        InsertUser insertUser = new InsertUser();
        insertUser.insertUser();
    }
    public void insertUser() throws SQLException {
        Connection connector = MySQLConnector.getConnection();
        String sql = "INSERT INTO users (user_name) VALUES ('test')";
        PreparedStatement statement = connector.prepareStatement(sql);
        statement.execute();
        connector.close();
    }

}
