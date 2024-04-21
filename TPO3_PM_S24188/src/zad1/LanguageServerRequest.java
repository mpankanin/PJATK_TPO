package zad1;

import java.io.Serializable;

public class LanguageServerRequest implements Serializable {

    private final String languageCode;
    private final int serverPort;

    public LanguageServerRequest(String languageCode, int serverPort) {
        this.languageCode = languageCode;
        this.serverPort = serverPort;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getServerPort() {
        return serverPort;
    }

}
