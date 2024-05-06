package zad1.Client;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.RequestType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

    public Client(int serverPort, String host) {
        start(new InetSocketAddress(host, serverPort));
        charset = Charset.defaultCharset();
        subscriptions = new ArrayList<>();
        new ClientGUI(this);
    }

    public static void main(String[] args) {
        new Client(20100, "localhost");
    }

    private void start(InetSocketAddress inetAdr){
        GlobalLogger.getLogger().info("[Client] - connecting to the server");
        try {
            this.channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(inetAdr);
            readRequests();
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
            default -> GlobalLogger.getLogger().warning("[Client] - Couldn't find an operation: " + request.getType());
        }
    }

    private void sendRequest(Request request){
        GlobalLogger.getLogger().info("[Client] - sending request to: " + channel.toString());
        try {
            if(!channel.isConnected()){
                while (!channel.finishConnect()){

                }
            }
            String jsonString = request.toJson().toString();
            ByteBuffer buffer = charset.encode(jsonString);
            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("[Client] - " + e);
        }
        GlobalLogger.getLogger().info("[Client] - request has been sent to: " + channel.toString());
    }

    public void requestSubscribe(String topic){
        if(topic == null){
            GlobalLogger.getLogger().info("[Client] - requestSubscription - input data is null");
            return;
        }

        Request request = new Request(RequestType.SUBSCRIBE, topic);
        sendRequest(request);
    }

    private void subscribe(Request request){
        if (request.getMessage() == null){
            GlobalLogger.getLogger().severe("[Client] - Subscribe request is empty");
            return;
        }

        subscriptions.add(request.getMessage());
        GlobalLogger.getLogger().info("[Client] - Subscription has been added: " + request.getMessage());
    }

    public void requestUnsubscribe(String topic){
        if(topic == null){
            GlobalLogger.getLogger().info("[Client] - request unsubscribe - input data is null");
            return;
        }

        Request request = new Request(RequestType.UNSUBSCRIBE, topic);
        sendRequest(request);
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
