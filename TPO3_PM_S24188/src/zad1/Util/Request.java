package zad1.Util;

import java.io.Serializable;

public class Request implements Serializable {

    private final String sentenceToTranslate;
    private final String languageCode;
    private final Integer port;
    private final Integer serverPort;
    private final RequestType requestType;

    public Request(String sentenceToTranslate, String languageCode, Integer port, Integer serverPort, RequestType requestType) {
        this.sentenceToTranslate = sentenceToTranslate;
        this.languageCode = languageCode;
        this.port = port;
        this.serverPort = serverPort;
        this.requestType = requestType;
    }

    public String getSentenceToTranslate() {
        return sentenceToTranslate;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public RequestType getRequestType() {
        return requestType;
    }

}
