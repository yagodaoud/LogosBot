package main.java.db;

import main.java.DiscordBot;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserListGetter {

    private static final List<Member> members = DiscordBot.getMembers();
    private static final List<String> membersString = new ArrayList<>();

    public static List<String> membersValue() {
        for (Member member : members){
            membersString.add(member.getUser().getName());
        } return membersString;
    }


}
