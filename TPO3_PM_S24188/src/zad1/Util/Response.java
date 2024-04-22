package zad1.Util;

import java.io.Serializable;

public class Response implements Serializable {

    private final String response;
    private final Integer targetPort;
    private final ResponseCode responseCode;
    private final String serverInfo;

    public Response(String response, Integer targetPort, ResponseCode responseCode, String serverInfo) {
        this.response = response;
        this.targetPort = targetPort;
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

    public int getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "Response{" +
                "response='" + response + '\'' +
                ", responseCode=" + responseCode +
                ", serverInfo='" + serverInfo + '\'' +
                '}';
    }

}
