package main.java;



import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;



import javax.security.auth.login.LoginException;

public class DiscordBot {

    private final Dotenv config;

    public DiscordBot() throws LoginException {

        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        JDA bot = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("Never Gonna Give You Up"))
                .addEventListeners(new BotEventListener())
                .addEventListeners(new BotCommands())
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
