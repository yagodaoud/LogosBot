import main.java.DiscordBot;
import main.java.commands.BotCommands;
import main.java.crypto.CryptoPriceDiscord;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;


public class JDATest {
    @Mock
    static DiscordBot bot;

    @Mock
    static SlashCommandInteractionEvent slashEvent;

    @Mock
    static CryptoPriceDiscord cryptoPrice;

    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(CryptoPriceDiscord.getPrice(Mockito.anyString())).thenReturn(100.0);
    }

    @Test
    public void testCryptoCommand() {
        setup();
        beforeEach();
        BotCommands botCommands = new BotCommands();
        botCommands.onSlashCommandInteraction(slashEvent);
        CryptoPriceDiscord.getPrice("BTC");

    }

    @AfterAll
    public static void cleanup() {
        bot.shutdown();
    }
}
