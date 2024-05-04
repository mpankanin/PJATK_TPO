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
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

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

    }

    private void sendRequest(Request request){

    }

    private void subscribe(String topic){

    }

    private void unsubscribe(String topic){

    }

    private void removeSubscription(String topic){

    }

}
