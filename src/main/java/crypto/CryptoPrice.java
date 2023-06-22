package main.java.crypto;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CryptoPrice { //Api connection and price getter class
    public String cryptoSymbol;
    private final String token;

    public CryptoPrice(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;
        Dotenv config = Dotenv.configure().load();
        this.token = config.get("TOKENCMC");

    }




    public double getPrice(String symbol) { //Coin symbol input from discord as argument
        try {
            URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbol); //Symbol is passed as url id for the coin wanted
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CMC_Pro_API_Key", token); //Make the connection

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
                        .getJSONObject(symbol)
                        .getJSONObject("quote")
                        .getJSONObject("USD");
                return symbolData.getDouble("price");
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
