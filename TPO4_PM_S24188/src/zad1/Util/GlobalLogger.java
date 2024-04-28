package zad1.Util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GlobalLogger {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        try {
            FileHandler fh = new FileHandler("src/zad1/TPO4.log");
            LOGGER.addHandler(fh);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fh.setFormatter(simpleFormatter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getLogger(){
        return LOGGER;
    }

}
