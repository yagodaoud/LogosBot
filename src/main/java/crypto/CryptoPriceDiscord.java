package main.java.crypto;

public class CryptoPriceDiscord {

    public static double getPrice(String symbol) {
        return ApiConnection.getPrice(symbol);
    }
}
