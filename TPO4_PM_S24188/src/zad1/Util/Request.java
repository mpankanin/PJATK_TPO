package zad1.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

public class Request implements Serializable {

    private RequestType type;
    private String message;

    public Request(RequestType type, String message) {
        this.type = type;
        this.message = message;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
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
        json.put("message", message);
        return json;
    }

    public static Request readRequest(SocketChannel channel, String logRole){
        if (!channel.isOpen()){
            return null;
        }
        GlobalLogger.getLogger().info(logRole + " - Reading a client's request.");
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        JSONObject json;
        Request request = null;
        try {
            StringBuilder jsonStringBuilder = new StringBuilder();
            buffer.clear();

            //Read loop
            int bytesRead = channel.read(buffer);
            if (bytesRead > 0) {
                GlobalLogger.getLogger().info(logRole + " - Read " + bytesRead + " bytes.");
                buffer.flip();
                while (buffer.hasRemaining()) {
                    jsonStringBuilder.append((char) buffer.get());
                }
            }

            json = new JSONObject(jsonStringBuilder.toString());

            request = new Request(
                    getRequestType(json),
                    getRequestMessage(json)
                    );
        } catch (IOException e) {
            GlobalLogger.getLogger().severe(e.toString());
        }

        GlobalLogger.getLogger().info(logRole + " - The client's request has been read: " + request);
        return request;
    }

    private static RequestType getRequestType(JSONObject json){
        Optional<RequestType> requestType = Arrays.stream(RequestType.values())
                .filter(rt -> rt.equals(json.get("type")))
                .findFirst();

        if (requestType.isEmpty()) {
            GlobalLogger.getLogger().severe("[Client] - Couldn't find Request type");
            return null;
        }else {
            return requestType.get();
        }
    }

    public static void sendRequest(Request request, SocketChannel channel, Charset charset, String logRole){
        GlobalLogger.getLogger().info(logRole + " - sending request to: " + channel.toString());
        try {
            if(!channel.isConnected()){
                while (!channel.finishConnect()){
                    //do something
                }
            }
            String jsonString = request.toJson().toString();
            ByteBuffer buffer = charset.encode(jsonString);
            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (IOException e) {
            GlobalLogger.getLogger().severe(logRole + " - " + e);
        }
        GlobalLogger.getLogger().info(logRole + " - request has been sent to: " + channel);
    }

    private static String getRequestMessage(JSONObject json){
        return json.getString("message");
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }

}
