/**
 *
 *  @author Pankanin Miłosz S24188
 *
 */

package zad1;


public class Main {
  public static void main(String[] args) {
    Service s = new Service("United Kingdom");
    String weatherJson = s.getWeather("London");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    // ...
    // część uruchamiająca GUI
  }
}
