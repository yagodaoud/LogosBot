package main.java.commands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;



import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class BotEventListener extends ListenerAdapter implements BotStatusObserver {

    private UserUpdateOnlineStatusEvent onlineStatusEvent = null;
    private int onlineMembers = 0;
    private List<Consumer<Integer>> onlineStatusChangeListeners = new ArrayList<>();
    private OnlineStatusChangeEvent statusChangeEvent = new OnlineStatusChangeEvent();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        else {
            if (event.getMessage().getContentDisplay().equalsIgnoreCase("$help")) {
                event.getChannel().sendMessage("See all commands below: ").queue();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel().sendMessage("reacted").queue();
    }

    public void addOnlineStatusChangeListener(Consumer<Integer> listener) {
        onlineStatusChangeListeners.add(listener);
    }

    public void removeOnlineStatusChangeListener(Consumer<Integer> listener) {
        onlineStatusChangeListeners.remove(listener);
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        System.out.println("test 1");
        onlineStatusEvent = event;
        int onlineMembers = getQuantityOnlineMembers();
        for (Consumer<Integer> listener : onlineStatusChangeListeners) {
            listener.accept(onlineMembers);
        }
    }

    public int getQuantityOnlineMembers() {
        System.out.println("test 2");
        if (onlineStatusEvent == null) {
            return 0;
        }
        System.out.println("test 3");
        List<Member> members = onlineStatusEvent.getGuild().getMembers();
        int onlineMembers = 0;
        for (Member member : members) {
            if (member.getOnlineStatus() == OnlineStatus.ONLINE) {
                onlineMembers++;
                System.out.println(onlineMembers);
            }
        }
        return onlineMembers;
    }

    @Override
    public void onBotStatusChange(int onlineMembers) {
        this.onlineMembers = onlineMembers;
    }

}