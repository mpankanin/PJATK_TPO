package zad1.MainServer;

import org.json.JSONObject;
import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.RequestType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class MainServer {

    private final String host;
    private final int port;

    private Set<String> availableTopics;
    private Map<SocketChannel, Set<String>> usersSubscriptions;
    private final Charset charset;

    public MainServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.availableTopics = new HashSet<>();
        this.usersSubscriptions = new HashMap<>();
        this.charset = Charset.defaultCharset();
        start();
    }


    public static void main(String[] args) {
        new MainServer("localhost", 20100);
    }

    public void start(){
        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
            socketChannel.socket().bind(new InetSocketAddress(host, port));
            socketChannel.configureBlocking(false);

            Selector selector = Selector.open();

            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            GlobalLogger.getLogger().info("[MainServer] - Waiting for requests.");
            while (true){
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                GlobalLogger.getLogger().info("[MainServer] - Received operations to execute - iterating through a set.");
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()){
                        SocketChannel clientChannel = socketChannel.accept();
                        usersSubscriptions.put(clientChannel, new HashSet<>());
                        GlobalLogger.getLogger().info("[MainServer] - Connection accepted: " + clientChannel.toString());

                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        continue;
                    }

                    if (key.isReadable()){
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        Request request = Request.readRequest(clientChannel, "[MainServer]");
                        serveRequest(request);
                    }
                }
            }
        } catch (IOException e) {
            GlobalLogger.getLogger().severe(e.toString());
        }
    }

    private void writeResponse(int port, JSONObject response){
        GlobalLogger.getLogger().info("[MainServer] - Writing a response to: " + port);
        try (SocketChannel channel = SocketChannel.open()){
            channel.configureBlocking(false);

            channel.connect(new InetSocketAddress("localhost", port));
            channel.write(charset.encode(response.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GlobalLogger.getLogger().info("[MainServer] - Message has been sent successfully to: " + port);
    }

    private void serveRequest(Request request){
        if (request == null || request.getType() == null){
            return;
        }

        switch (request.getType()){
            case SUBSCRIBE, UNSUBSCRIBE -> manageUserSubscription(request);
            case REMOVE_SUBSCRIPTION -> removeSubscription(request);
            case ADD_TOPIC -> addTopic(request);
            case REMOVE_TOPIC -> removeTopic(request);
            case PUBLISH_NEWS -> publishNews(request);
            default -> GlobalLogger.getLogger().warning("[MainServer] - Couldn't find an operation: " + request.getType());
        }
    }

    private void removeSubscription(Request request) {
//        try {
//            String[] splitMessage = request.getMessage().split(";");
//            Integer port = Integer.parseInt(splitMessage[0]);
//            Set<String> subscriptions = usersSubscriptions.get(port);
//            subscriptions.remove(splitMessage[1]);
//            usersSubscriptions.put(port, subscriptions);
//        } catch (Exception e){
//            GlobalLogger.getLogger().severe("[MainServer] - " + e);
//        }
    }

    private void publishNews(Request request) {
        if (request.getMessage() != null){
            try {
                GlobalLogger.getLogger().info(request.getMessage());
                String[] splitMessage = request.getMessage().split(";");
                GlobalLogger.getLogger().info(Arrays.toString(splitMessage));
                List<Integer> portsToSend = getUserSocketsForTopic(splitMessage[0]);
                GlobalLogger.getLogger().info(portsToSend.toString());
                String news = splitMessage[1];
                for (Integer port : portsToSend){
                    Request response = new Request(RequestType.SERVER_NEWS, news);
                    writeResponse(port, response.toJson());
                }
            } catch (Exception e){
                GlobalLogger.getLogger().severe("[MainServer] - Couldn't parse received message: " + request.getMessage());
            }
        }
    }

    private void removeTopic(Request request) {
        if (request.getMessage() != null){
            availableTopics.remove(request.getMessage());
        }
    }

    private void addTopic(Request request) {
        if (request.getMessage() != null){
           availableTopics.add(request.getMessage());
        }
    }

    private void manageUserSubscription(SocketChannel socket, Request request) {
        if (request.getMessage() != null) {
            String topic = request.getMessage();
            if (topic != null) {
                switch (request.getType()) {
                    case SUBSCRIBE -> addUserSubscription(socket, topic);
                    case UNSUBSCRIBE -> removeUserSubscription(socket, topic);
                }
            }
        }
    }

    private void addUserSubscription(SocketChannel socket, String topic){
        GlobalLogger.getLogger().info("[MainServer] - Adding user's subscription (" + socket + ", " + topic + ')');
        Set<String> subscriptions = usersSubscriptions.get(socket);
        if (subscriptions == null){
            subscriptions = new HashSet<>();
        }
        subscriptions.add(topic);
        usersSubscriptions.put(socket, subscriptions);
    }

    private void removeUserSubscription(SocketChannel socket, String topic){
        GlobalLogger.getLogger().info("[MainServer] - Removing user's subscription (" + socket + ", " + topic + ')');
        Set<String> subscriptions = usersSubscriptions.get(socket);
        if (subscriptions != null){
            subscriptions.remove(topic);
        }
    }

    private List<SocketChannel> getUserSocketsForTopic(String topic) {
        List<SocketChannel> userSockets = new ArrayList<>();
        for (Map.Entry<SocketChannel, Set<String>> entry : usersSubscriptions.entrySet()) {
            if (entry.getValue().contains(topic)) {
                userSockets.add(entry.getKey());
            }
        }
        return userSockets;
    }

}
