package main.java;

import io.github.cdimascio.dotenv.Dotenv;
import main.java.commands.BotCommands;
import main.java.commands.BotEventListener;
import main.java.commands.CustomActivity;
import main.java.db.InsertDashboardUser;
import main.java.db.InsertUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordBot extends ListenerAdapter {

    private final Dotenv config;
    private final BotEventListener eventListener;
    private static JDA bot;
    private static List<Member> members;

    public DiscordBot() throws LoginException {
        config = Dotenv.configure().load();
        String token = config.get("TOKENDISCORD");
        eventListener = new BotEventListener();

        bot = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_VOICE_STATES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(EnumSet.of(
                        CacheFlag.ONLINE_STATUS
                ))
                .addEventListeners(new BotCommands())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        bot.getGatewayPool().scheduleAtFixedRate(() -> {
            int onlineMembers = eventListener.getQuantityOnlineMembers();
            //bot.getPresence().setActivity(Activity.listening("Users online: " + onlineMembers));
            bot.getPresence().setActivity(Activity.listening("/help"));
        }, 0, 60, TimeUnit.SECONDS);

        bot.addEventListener(this);
    }

    public static void main(String[] args) {
        try {
            DiscordBot discordBot = new DiscordBot();
        } catch (LoginException e) {
            System.out.println("Invalid token");
        }
    }

    public static List<Member> getMembers() {
        if (members == null) {
            members = bot.getGuilds().stream()
                    .flatMap(guild -> guild.getMembers().stream())
                    .collect(Collectors.toList());
        }
        return members;
    }

    public static List<String> getDashboardMembers() {
        return BotCommands.memberList.stream().toList();
    }


    public void shutdown() {
        bot.shutdown();
    }
}

