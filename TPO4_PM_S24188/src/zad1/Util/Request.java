package zad1.Util;

import java.io.Serializable;

public class Request implements Serializable {

    private RequestType type;
    private int responsePort;
    private String message;

    public Request(RequestType type, int responsePort, String message) {
        this.type = type;
        this.responsePort = responsePort;
        this.message = message;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public int getResponsePort() {
        return responsePort;
    }

    public void setResponsePort(int responsePort) {
        this.responsePort = responsePort;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
