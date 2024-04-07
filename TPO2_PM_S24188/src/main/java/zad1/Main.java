/**
 *
 *  @author Pankanin Miłosz S24188
 *
 */

package zad1;


import javax.swing.*;

public class Main {

  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("EUR");
    Double rate2 = s.getNBPRate();

    //staring GUI
    SwingUtilities.invokeLater(() -> new GUI(weatherJson, rate1, rate2));
  }
}
