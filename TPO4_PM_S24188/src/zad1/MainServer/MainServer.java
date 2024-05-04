package zad1.MainServer;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

public class MainServer {

    private final String host;
    private final int port;

    private Set<String> availableTopics;
    private Map<Integer, Set<String>> usersSubscriptions;
    private Charset charset = Charset.defaultCharset();

    public MainServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.availableTopics = new HashSet<>();
        this.usersSubscriptions = new HashMap<>();
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
                        GlobalLogger.getLogger().info("[MainServer] - Connection accepted: " + clientChannel.toString());

                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        continue;
                    }

                    if (key.isReadable()){
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        Request request = readRequest(clientChannel);
                        serveRequest(request);
                    }
                }
            }
        } catch (IOException e) {
            GlobalLogger.getLogger().severe(e.toString());
        }
    }

    private Request readRequest(SocketChannel channel){
        if (!channel.isOpen()){
           return null;
        }
        GlobalLogger.getLogger().info("[MainServer] - Reading a client's request.");

        Request request = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(Channels.newInputStream(channel))){
            request = (Request) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            GlobalLogger.getLogger().severe(e.toString());
        }

        GlobalLogger.getLogger().info("[MainServer] - The client's request has been read.");
        return request;
    }

    private void writeResponse(int port, String response){
        GlobalLogger.getLogger().info("[MainServer] - Writing a response to: " + port);
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);

            channel.connect(new InetSocketAddress("localhost", port));
            channel.write(charset.encode(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GlobalLogger.getLogger().info("[MainServer] - Message has been sent successfully to: " + port);
    }

    private void serveRequest(Request request){
        if (request == null){
            return;
        }

        switch (request.getType()){
            case SUBSCRIBE, UNSUBSCRIBE -> manageUserSubscription(request);
            case REMOVE_SUBSCRIPTION -> removeSubscription(request);
            case ADD_TOPIC -> addTopic(request);
            case REMOVE_TOPIC -> removeTopic(request);
            case NEWS -> publishNews(request);
            default -> GlobalLogger.getLogger().warning("[MainServer] - Couldn't find an operation: " + request.getType());
        }
    }

    private void removeSubscription(Request request) {
        try {
            String[] splitMessage = request.getMessage().split(";");
            Integer port = Integer.parseInt(splitMessage[0]);
            Set<String> subscriptions = usersSubscriptions.get(port);
            subscriptions.remove(splitMessage[1]);
            usersSubscriptions.put(port, subscriptions);
        } catch (Exception e){
            GlobalLogger.getLogger().severe("[MainServer] - " + e);
        }
    }

    private void publishNews(Request request) {
        if (request.getMessage() != null){
            try {
                String[] splitMessage = request.getMessage().split(";");
                List<Integer> portsToSend = getUserIdsForTopic(splitMessage[0]);
                String news = splitMessage[1];
                for (Integer port : portsToSend){
                    writeResponse(port, news);
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
        if (request.getMessage() != null && !availableTopics.contains(request.getMessage())){
           availableTopics.add(request.getMessage());
        }
    }

    private void manageUserSubscription(Request request) {
        int userId;
        String topic;
        if (request.getMessage() != null){
            try {
                String[] splitMessage = request.getMessage().split(";");
                userId = Integer.parseInt(splitMessage[0]);
                topic = splitMessage[1];
                if(topic != null){
                    switch (request.getType()){
                        case SUBSCRIBE -> addUserSubscription(userId, topic);
                        case UNSUBSCRIBE -> removeUserSubscription(userId, topic);
                    }
                }
            } catch (Exception e){
                GlobalLogger.getLogger().severe("[MainServer] - Couldn't parse received message: " + request.getMessage());
            }
        }
    }

    private void addUserSubscription(Integer userId, String topic){
        GlobalLogger.getLogger().info("[MainServer] - Adding user's subscription (" + userId + ", " + topic + ')');
        Set<String> subscriptions = usersSubscriptions.get(userId);
        if (subscriptions == null){
            subscriptions = new HashSet<>();
        }
        subscriptions.add(topic);
        usersSubscriptions.put(userId, subscriptions);
    }

    private void removeUserSubscription(Integer userId, String topic){
        GlobalLogger.getLogger().info("[MainServer] - Removing user's subscription (" + userId + ", " + topic + ')');
        Set<String> subscriptions = usersSubscriptions.get(userId);
        if (subscriptions != null){
            subscriptions.remove(topic);
        }
    }

    private List<Integer> getUserIdsForTopic(String topic) {
        List<Integer> userIds = new ArrayList<>();
        for (Map.Entry<Integer, Set<String>> entry : usersSubscriptions.entrySet()) {
            if (entry.getValue().contains(topic)) {
                userIds.add(entry.getKey());
            }
        }
        return userIds;
    }

}
