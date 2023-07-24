package main.java.db;

import main.java.commands.BotCommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InsertDashboardUser {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL = 3600;

    public static void insertUsers() throws SQLException {
        executorService.scheduleAtFixedRate(() -> {
            try {
                Connection connector = MySQLConnector.getConnection();

                List<String> dashboardMembersList = BotCommands.memberList;
                List<String> commandList = BotCommands.commandUsedByMemberList;
                List<String> guildList = BotCommands.guildMemberWasInList;
                List<Timestamp> timestampList = BotCommands.timestampList;
                System.out.println(timestampList);
                System.out.println(commandList);
                System.out.println(guildList);
                System.out.println(dashboardMembersList);

                String sql = "INSERT INTO dashboardUsers (user_name, command, guild, created_at) VALUES (?, ?, ?, ?)";

                PreparedStatement statement = connector.prepareStatement(sql);

                for (int i = 0; i < dashboardMembersList.size(); i++) {
                    String userName = dashboardMembersList.get(i);
                    String command = commandList.get(i);
                    String guild = guildList.get(i);
                    Timestamp timestamp = timestampList.get(i);

                    statement.setString(1, userName);
                    statement.setString(2, command);
                    statement.setString(3, guild);
                    statement.setTimestamp(4, timestamp);

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

                System.out.println(numInserted + " users with command inserted");
                dashboardMembersList.clear();
                commandList.clear();
                guildList.clear();
                timestampList.clear();

            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
            }

        }, 0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }
}


