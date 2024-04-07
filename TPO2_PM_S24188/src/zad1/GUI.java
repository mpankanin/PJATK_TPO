package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class GUI {

    private final String wikiUrl = "https://en.wikipedia.org/wiki/%s";
    private JSONObject weatherJSON;

    private JLabel weatherLabel;
    private JLabel rateLabel;
    private JLabel nbpRateLabel;
    private WebView webView;


    public GUI(String weatherString, Double rate, Double nbpRate) {
        weatherJSON = new JSONObject(weatherString);
        initAndShowGUI(rate, nbpRate);
    }

    private void initAndShowGUI(Double rate, Double nbpRate) {
        //basic frame settings
        JFrame frame = new JFrame("TPO_2-s24188");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //panel for text data and button
        final JPanel textFieldsPanel = new JPanel();
        textFieldsPanel.setLayout(new FlowLayout());

        //text fields
        weatherLabel = new JLabel(getWeatherDescription());
        weatherLabel.setBorder(BorderFactory.createTitledBorder("Weather"));
        weatherLabel.setPreferredSize(new Dimension(150, 150));

        rateLabel = new JLabel(getRateDescription(rate));
        rateLabel.setBorder(BorderFactory.createTitledBorder("Currency rate"));
        rateLabel.setPreferredSize(new Dimension(150, 150));

        nbpRateLabel = new JLabel(getNbpRateDescription(nbpRate));
        nbpRateLabel.setBorder(BorderFactory.createTitledBorder("PLN rate"));
        nbpRateLabel.setPreferredSize(new Dimension(150, 150));

        textFieldsPanel.add(weatherLabel);
        textFieldsPanel.add(rateLabel);
        textFieldsPanel.add(nbpRateLabel);

        //change data button
        JButton changeDataButton = new JButton("Change data");
        changeDataButton.setPreferredSize(new Dimension(150, 150));

        changeDataButton.addActionListener(e -> {
            try {
                updateData();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Provided data is invalid", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        textFieldsPanel.add(changeDataButton);

       //website engine
        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(800, 600));
        Platform.runLater(() -> {
            webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load(String.format(wikiUrl, weatherJSON.get("name")));

            Scene scene = new Scene(webView);
            fxPanel.setScene(scene);
        });

        //final settings
        frame.add(textFieldsPanel, BorderLayout.NORTH);
        frame.add(fxPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String getWeatherDescription(){
        StringBuilder weatherDescription = new StringBuilder();

        weatherDescription.append("<html><pre>");
        weatherDescription.append("Location: " + weatherJSON.get("name") + '\n');
        //weatherDescription.append("Sky: " + weatherJSON.getJSONObject("weather").get("description"));
        weatherDescription.append("Temperature: " + weatherJSON.getJSONObject("main").get("temp") + '\n');
        weatherDescription.append("Pressure: " + weatherJSON.getJSONObject("main").get("pressure") + '\n');
        weatherDescription.append("Humidity: " + weatherJSON.getJSONObject("main").get("humidity") + '\n');
        weatherDescription.append("Wind: " + weatherJSON.getJSONObject("wind").get("speed"));
        weatherDescription.append("</pre></html>");

        return weatherDescription.toString();
    }

    private String getRateDescription(Double rate){
        StringBuilder rateDescription = new StringBuilder();

        rateDescription.append("<html><pre>");
        rateDescription.append(rate);
        rateDescription.append("</pre></html>");

        return rateDescription.toString();
    }

    private String getNbpRateDescription(Double nbpRate){
        StringBuilder rateDescription = new StringBuilder();

        rateDescription.append("<html><pre>");
        rateDescription.append(nbpRate);
        rateDescription.append("</pre></html>");

        return rateDescription.toString();
    }

    private void updateGUI(String weatherJson, Double rate, Double nbpRate) {
        // Update the weatherJSON object with the new data
        this.weatherJSON = new JSONObject(weatherJson);

        // Update the labels
        weatherLabel.setText(getWeatherDescription());
        rateLabel.setText(getRateDescription(rate));
        nbpRateLabel.setText(getNbpRateDescription(nbpRate));

        // Reload the WebView with the new city's Wikipedia page
        Platform.runLater(() -> {
            WebEngine webEngine = webView.getEngine();
            webEngine.load(String.format(wikiUrl, weatherJSON.get("name")));
        });
    }

    private void updateData(){
        JTextField countryField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField currencyField = new JTextField();
        Object[] message = {
                "Country:", countryField,
                "City:", cityField,
                "Currency:", currencyField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Enter new location", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String updatedCountry = countryField.getText();
            String updatedCity = cityField.getText();
            String updatedCurrency = currencyField.getText();

            Service s = new Service(updatedCountry);
            String weatherJson = s.getWeather(updatedCity);
            Double updatedRate = s.getRateFor(updatedCurrency);
            Double rate2 = s.getNBPRate();

            updateGUI(weatherJson, updatedRate, rate2);
        }
    }
}




