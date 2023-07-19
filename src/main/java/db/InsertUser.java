package main.java.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InsertUser {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL= 3600;

    public static void insertUsers() throws SQLException {
        executorService.scheduleAtFixedRate(() -> {
            try {

                Connection connector = MySQLConnector.getConnection();
                List<String> userList = UserListGetter.membersValue();

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
            } catch (SQLException exception){
                System.out.println(exception.getMessage());
            }

        },0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }
}
