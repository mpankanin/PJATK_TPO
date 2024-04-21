package zad1;

import java.io.Serializable;

public class Request implements Serializable {

    private final String sentenceToTranslate;
    private final String languageCode;
    private final int port;

    public Request(String sentenceToTranslate, String languageCode, int port) {
        this.sentenceToTranslate = sentenceToTranslate;
        this.languageCode = languageCode;
        this.port = port;
    }

    public String getSentenceToTranslate() {
        return sentenceToTranslate;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getPort() {
        return port;
    }

}
