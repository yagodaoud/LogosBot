package main.java;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CryptoPrice {
    private String cryptoSymbol;

    public CryptoPrice(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;

    }

    public  double getPrice(String symbol) {
        try {
            String symbolCrypto = symbol;
            URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbolCrypto);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CMC_Pro_API_Key", "CoinMarketApi Token here");

            int status = connection.getResponseCode();
            if (status == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject symbolData = json.getJSONObject("data")
                        .getJSONObject(symbolCrypto)
                        .getJSONObject("quote")
                        .getJSONObject("USD");
                double price = symbolData.getDouble("price");
                return price;
            } else {
                System.err.println("Failed to fetch data, status code: " + status);
                return 0.0;
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return 0.1;
        }
    }
}
