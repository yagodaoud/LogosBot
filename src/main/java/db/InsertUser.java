package main.java.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertUser {
    public static void insertUsers() throws SQLException {
        Connection connector = MySQLConnector.getConnection();
        List<String> userList = UserListGetter.membersValue();
        System.out.println(userList);

        String sql = "INSERT INTO users (user_name) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = ?)";

        PreparedStatement statement = connector.prepareStatement(sql);

        for (String userName : userList) {
            statement.setString(1, userName);
            statement.setString(2, userName);
            statement.addBatch();
        }

        int[] result = statement.executeBatch();
        connector.close();

        int numInserted = 0;
        for (int i : result) {
            if (i == PreparedStatement.SUCCESS_NO_INFO || i > 0) {
                numInserted++;
            }
        }

        System.out.println(numInserted + " users inserted.");
    }
}
