package main.java.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;



import org.jetbrains.annotations.NotNull;


public class BotEventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {


        if (event.getAuthor().isBot())
            return;
        else {
            if (event.getMessage().getContentDisplay().equalsIgnoreCase("$help")) {
                event.getChannel().sendMessage("See all commands below:  ").queue();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        event.getTextChannel().sendMessage("reacted").queue();
    }

}


