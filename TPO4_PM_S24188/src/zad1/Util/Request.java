package zad1.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class Request implements Serializable {

    private RequestType type;
    private int responsePort;
    private String message;

    public Request(RequestType type, Integer responsePort, String message) {
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

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("responsePort", responsePort);
        json.put("message", message);
        return json;
    }

    public static Request readRequest(SocketChannel channel, String logRole){
        if (!channel.isOpen()){
            return null;
        }
        GlobalLogger.getLogger().info(logRole + " - Reading a client's request.");

        Request request = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(Channels.newInputStream(channel))){
            request = (Request) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            GlobalLogger.getLogger().severe(e.toString());
        }

        GlobalLogger.getLogger().info(logRole + " - The client's request has been read.");
        return request;
    }

}
