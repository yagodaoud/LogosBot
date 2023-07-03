package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BitcoinPriceAlertTest {

    @Test
    void variationEqualsOnePercent(){
        var bitcoinAlert = new BitcoinPriceAlert();
        assertEquals(1, bitcoinAlert.priceVariationCalculator(30300, 30000));
    }

    @Test
    void variationMessageTest() {
        TextChannel mockTextChannel = Mockito.mock(TextChannel.class);

        BitcoinPriceAlert bitcoinAlert = Mockito.spy(new BitcoinPriceAlert(1.0));
        Mockito.doReturn(30300.0).when(bitcoinAlert).BitcoinPriceGetter();
        bitcoinAlert.startAlert(mockTextChannel);

        bitcoinAlert.stopAlert();
    }
}