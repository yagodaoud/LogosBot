package main.java;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test {



    public static void main(String[] args) {
            try {
                String symbol = "ETH";
                URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbol);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-CMC_Pro_API_Key", "e77bacb5-8443-4bc7-8f5b-e0e26b497abd");

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
                    double price = symbolData.getDouble("price");
                    System.out.println(price);
                } else {
                    System.err.println("Failed to fetch data, status code: " + status);
                    return;
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                return;
            }
            return;
        }
    }


