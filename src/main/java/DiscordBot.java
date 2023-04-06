package main.java;



import io.github.cdimascio.dotenv.Dotenv;
import main.java.commands.BotCommands;
import main.java.commands.BotEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class DiscordBot {

    private final Dotenv config;

    public DiscordBot() throws LoginException {

        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        JDA bot = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES
                )
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                ))
                .setActivity(Activity.listening("Never Gonna Give You Up"))
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .addEventListeners(new BotEventListener())
                .addEventListeners(new BotCommands())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();

    }
    public Dotenv getConfig() {
        return config;
    }

    public static void main(String[] args) {
        try {
            DiscordBot discordBot = new DiscordBot();
        } catch (LoginException e) {
            System.out.println("Invalid token");;
        }


    }
}
