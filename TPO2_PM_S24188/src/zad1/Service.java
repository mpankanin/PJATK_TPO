/**
 *
 *  @author Pankanin Miłosz S24188
 *
 */

package zad1;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Service {

    private final Locale locale;
    private final Currency localCurrency;
    private final String weatherApiKey = "7d5145dcf76f5b018c2ba8c35d355530";
    private final String currencyApiKey = "c0232a488d2e6782e4781e6e";
    private final String weatherApiUrlName = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
    private final String locationApiUrlName = "http://api.openweathermap.org/geo/1.0/direct?q=%s,%s&limit=1&appid=%s";
    private final String currencyApiUrlName = "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s";
    private final String nbpApiUrlName = "http://api.nbp.pl/api/exchangerates/rates/A/%s";


    public Service(String countryName) {
        locale = getCountry(countryName);
        localCurrency = Currency.getInstance(locale);
    }

    public String getWeather(String cityName) {
        try {
            //getting location lat and lon from API
            URL locationApiUrl = new URL(String.format(locationApiUrlName, cityName, locale.getISO3Country(), weatherApiKey));
            String locationString = readFromUrl(locationApiUrl);
            JSONObject locationJSON = new JSONObject(locationString.substring(1, locationString.length()-1));

            //getting weather data from API based on lat and lon
            Double lat = Double.parseDouble(locationJSON.get("lat").toString());
            Double lon = Double.parseDouble(locationJSON.get("lon").toString());
            URL weatherApiUrl = new URL(String.format(weatherApiUrlName, String.format("%.2f", lat), String.format("%.2f", lon), weatherApiKey));

            return readFromUrl(weatherApiUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Double getRateFor(String currency) {
        try {
            URL  currencyApiUrl = new URL(String.format(currencyApiUrlName, currencyApiKey, localCurrency, currency));
            JSONObject currencyRateJSON = new JSONObject(readFromUrl(currencyApiUrl));

            return currencyRateJSON.getDouble("conversion_rate");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Double getNBPRate() {
        if(localCurrency.getCurrencyCode().equals("PLN"))
            return 1.0;
        else {
            try {
                URL nbpApiUrl = new URL(String.format(nbpApiUrlName, localCurrency));
                JSONObject nbpRateJSON = new JSONObject(readFromUrl(nbpApiUrl));

                return nbpRateJSON.getJSONArray("rates").getJSONObject(0).getDouble("mid");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Locale getCountry(String countryName){
        List<Locale> countries = List.of(Locale.getAvailableLocales());
        Optional<Locale> receivedCountry = countries.stream().filter(country -> country.getDisplayCountry().equalsIgnoreCase(countryName)).findFirst();
        return receivedCountry.orElse(null);
    }

    public static String readFromUrl(URL url) {
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
