package main.java;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.time.*;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class test{

    public static void main(String[] args) {

        String url = "https://buybitcoinworldwide.com/halving/";
        try {
            Document doc = Jsoup.connect(url).get();
            Element nextHalvingScrape = doc.getElementById("date_eta");
            System.out.println(nextHalvingScrape);
            String a = nextHalvingScrape.text();
            System.out.println(a);
        } catch (IOException ioException){
            ioException.getMessage();
        }

        LocalDate today = LocalDate.now();
        LocalDate nextHalving = LocalDate.of(2024, Month.APRIL, 5);
        long daysBetweenNextHalving = ChronoUnit.DAYS.between(today, nextHalving);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("Days to next halving: " + daysBetweenNextHalving + "\nHalving date ETA: " + nextHalving.format(formatter));
    }

}