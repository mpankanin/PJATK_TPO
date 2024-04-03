/**
 *
 *  @author Pankanin Miłosz S24188
 *
 */

package zad1;


import com.github.cliftonlabs.json_simple.Jsonable;

import java.io.IOException;
import java.io.Writer;

public class Service implements Jsonable {

    public Service() {
    }

    public Service(String poland) {

    }

    public String getWeather(String warsaw) {
        return null;
    }


    public Double getRateFor(String usd) {
        return null;
    }

    public Double getNBPRate() {
        return null;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public void toJson(Writer writer) throws IOException {

    }
}
