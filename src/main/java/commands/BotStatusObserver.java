package main.java.commands;

public interface BotStatusObserver {
    void onBotStatusChange(int onlineMembers);
}