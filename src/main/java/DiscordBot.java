package main.java;



import io.github.cdimascio.dotenv.Dotenv;
import main.java.commands.BotCommands;
import main.java.commands.BotEventListener;
import main.java.commands.CustomActivity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class DiscordBot {

    private final Dotenv config;
    private final BotEventListener eventListener;
    private JDA bot;

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
                        CacheFlag.EMOTE
                ))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(EnumSet.of(
                        CacheFlag.ONLINE_STATUS
                ))
                .setActivity(Activity.listening(new CustomActivity(eventListener).getName()))
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .addEventListeners(eventListener)
                .addEventListeners(new BotCommands())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        bot.getGatewayPool().scheduleAtFixedRate(() -> {
            int onlineMembers = eventListener.getQuantityOnlineMembers();
            bot.getPresence().setActivity(Activity.listening("Users online: " + onlineMembers));
        }, 0, 60, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        try {
            DiscordBot discordBot = new DiscordBot();
        } catch (LoginException e) {
            System.out.println("Invalid token");
        }
    }
}
