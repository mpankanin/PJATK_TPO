package zad1.MainServer;

import com.sun.net.httpserver.Request;
import zad1.Util.GlobalLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MainServer {

    private final String host;
    private final int port;

    public MainServer(String host, int port) {
        this.host = host;
        this.port = port;
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
                        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                        continue;
                    }

                    if (key.isReadable()){
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        Request request = readRequest(clientChannel);
                        serveRequest(request);

                        continue;
                    }

                    if (key.isWritable()){
                       SocketChannel clientChannel = (SocketChannel) key.channel();
                       writeRequest(clientChannel);
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

    private void writeRequest(SocketChannel channel){
        GlobalLogger.getLogger().info("[MainServer] - Writing a request");

    }

    private void serveRequest(Request request){



    }



}
