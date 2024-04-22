package zad1;

import java.io.Serializable;

public class Response implements Serializable {

    private final String response;
    private final ResponseCode responseCode;
    private final String serverInfo;

    public Response(String response, ResponseCode responseCode, String serverInfo) {
        this.response = response;
        this.responseCode = responseCode;
        this.serverInfo = serverInfo;
    }

    public String getResponse() {
        return response;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

}
