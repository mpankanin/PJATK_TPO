package zad1.Client;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.RequestType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Client {

    private SocketChannel channel = null;
    private Charset charset;
    private List<String> subscriptions;
    private ClientGUI clientGUI;

    public Client(String host, int serverPort) {
        start(new InetSocketAddress(host, serverPort));
        charset = Charset.defaultCharset();
        subscriptions = new ArrayList<>();
        clientGUI = new ClientGUI(this);
        readRequests();
    }

    public static void main(String[] args) {
        new Client("localhost", 20100);
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
            case OK_SUBSCRIBE -> subscribe(request);
            case REMOVE_SUBSCRIPTION -> removeSubscription(request);
            case SERVER_NEWS -> clientGUI.appendMessage(request.getMessage());
            default -> GlobalLogger.getLogger().warning("[Client] - Couldn't find an operation: " + request.getType());
        }
    }

    public String requestSubscribe(String topic){
        if(topic == null){
            GlobalLogger.getLogger().info("[Client] - requestSubscription - input data is null");
            return "ERROR - requestSubscribe - provided topic is empty";
        }

        Request request = new Request(RequestType.SUBSCRIBE, topic);
        Request.sendRequest(request, channel, charset, "[Client]");
        return "OK - requestSubscribe - subscription request has been sent: " + topic;
    }

    private void subscribe(Request request){
        if (request.getMessage() == null){
            GlobalLogger.getLogger().severe("[Client] - Subscribe request is empty");
            return;
        }

        subscriptions.add(request.getMessage());
        GlobalLogger.getLogger().info("[Client] - Subscription has been added: " + request.getMessage());
    }

    public String requestUnsubscribe(String topic){
        if(topic == null){
            GlobalLogger.getLogger().info("[Client] - request unsubscribe - input data is null");
            return "ERROR - requestUnsubscribe - provided topic is empty";
        }

        Request request = new Request(RequestType.UNSUBSCRIBE, topic);
        Request.sendRequest(request, channel, charset, "[Client]");
        return "OK - requestUnsubscribe - request has been sent successfully: " + topic;
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
