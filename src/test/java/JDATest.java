package test.java;

import dev.coly.jdat.JDATesting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JDATest {
    @Test
    public void testAssertGuildMessageReceivedEvent() {
        JDATesting.assertGuildMessageReceivedEvent(new TestEventListener(), ".ping", "Pong!");
    }

    @Test
    public void testTestGuildMessageReceivedEvent() {
        try {
            assertEquals("Pong!", JDATesting.testGuildMessageReceivedEvent(new TestEventListener(), ".ping").getContentRaw());
        } catch (InterruptedException e) {
            fail(e);
        }
    }

    @Test
    public void testAssertSlashCommandEvent() {
        JDATesting.assertSlashCommandEvent(new TestEventListener(), "ping", new HashMap<>(), "Pong!");
    }

    @Test
    public void testAssertSlashCommandEventWithOptions() {
        Map<String, Object> map = new HashMap<>();
        map.put("bool", true);
        map.put("str", "text");
        map.put("number", 42);
        map.put("user", JDAObjects.getFakeUser());
        JDATesting.assertSlashCommandEvent(new TestEventListener(), "options", map,
                "bool: true - str: text - number: 42 - user: User#0000");
    }

    @Test
    public void testAssertSlashCommandEventWithEmbeds() {
        JDATesting.assertSlashCommandEvent(new TestEventListener(), "embed", new HashMap<>(),
                Collections.singletonList(TestEventListener.getTestEmbed()));
    }

    public static class TestEventListener implements EventListener {

        @Override
        public void onEvent(@NotNull GenericEvent event) {
            if (event instanceof GuildMessageReceivedEvent) {
                GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;
                if (e.getMessage().getContentDisplay().equals(".ping")) {
                    e.getChannel().sendMessage("Pong!").queue();
                }
            }
        }

        public static MessageEmbed getTestEmbed() {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Test Embed");
            embedBuilder.setAuthor("Coly Team");
            embedBuilder.addField("Test Name", "Test Value", true);
            return embedBuilder.build();
        }
    }
}
