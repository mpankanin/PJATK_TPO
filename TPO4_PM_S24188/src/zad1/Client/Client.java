package zad1.Client;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client {

    private SocketChannel channel = null;
    private Charset charset;
    private List<String> subscriptions;

    public Client(Integer serverPort, String host) {
        start(new InetSocketAddress(host, serverPort));
        charset = Charset.defaultCharset();
        subscriptions = new ArrayList<>();
    }

    public static void main(String[] args) {

    }

    private void start(InetSocketAddress inetAdr){
        GlobalLogger.getLogger().info("[Client] - connecting to the server");
        try {
            this.channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(inetAdr);
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("[Client] - " + e);
            System.exit(1);
        }
        GlobalLogger.getLogger().info("[Client] - connected to the server");
    }

    private void readRequests() {
        try {
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        Request request = Request.readRequest(clientChannel, "[Client]");
                        serveRequest(request);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void serveRequest(Request request) {
        if (request == null){
            return;
        }

        switch (request.getType()){
            case OK -> subscribe(request);
            case REMOVE_SUBSCRIPTION -> removeSubscription(request);
            default -> GlobalLogger.getLogger().warning("[Client] - Couldn't find an operation: " + request.getType());
        }
    }

    private void sendRequest(Request request){

    }

    private void requestSubscribtion(String topic){

    }

    private void subscribe(Request request){
        if (request.getMessage() == null){
            GlobalLogger.getLogger().severe("[Client] - Subscription request is empty");
            return;
        }

        subscriptions.add(request.getMessage());
        GlobalLogger.getLogger().info("[Client] - Subscription has been added: " + request.getMessage());
    }

    private void unsubscribe(String topic){

    }

    private void removeSubscription(Request request){
        if (request.getMessage() == null){
            GlobalLogger.getLogger().severe("[Client] - Remove subscription request is empty");
            return;
        }

        subscriptions.remove(request.getMessage());
        GlobalLogger.getLogger().info("[Client] - Subscription has been removed: " + request.getMessage());
    }

}
