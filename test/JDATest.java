import main.java.DiscordBot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;

public class JDATest {

    private static DiscordBot bot;

    @BeforeAll
    public static void setup() throws LoginException {
        bot = new DiscordBot();
    }

    @Test
    public void testBot() throws LoginException {
    }
    @AfterAll
    public static void cleanup() {
        bot.shutdown();
        }

}
