package zad1;

import java.io.Serializable;

public class Response implements Serializable {

    private final String response;
    private final String serverInfo;

    public Response(String response, String serverInfo) {
        this.response = response;
        this.serverInfo = serverInfo;
    }

    public String getResponse() {
        return response;
    }

    public String getServerInfo() {
        return serverInfo;
    }

}
