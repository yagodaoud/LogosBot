package main.java.crypto;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CryptoPrice { //Api connection and price getter class
    public String cryptoSymbol;

    public CryptoPrice(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;

    }


    public double getPrice(String symbol) { //Coin symbol input from discord as argument
        try {
            String symbolCrypto = symbol;
            URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbolCrypto); //Symbol is passed as url Id for the coin wanted
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CMC_Pro_API_Key", "YOUR_TOKEN_HERE"); //Make the connection

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
                double price = symbolData.getDouble("price"); //Get price from api url and return it to de displayed in BotCommands class
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
