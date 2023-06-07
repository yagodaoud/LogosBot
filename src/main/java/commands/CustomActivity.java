package main.java.commands;

import net.dv8tion.jda.internal.entities.ActivityImpl;


public class CustomActivity extends ActivityImpl {
    private BotEventListener eventListener;


    public CustomActivity(BotEventListener eventListener) {
        super(null);
        this.eventListener = eventListener;
    }

    @Override
    public String getName() {
        int onlineMembers = eventListener.getQuantityOnlineMembers();
        return "Users online: " + String.valueOf(onlineMembers);
    }
}