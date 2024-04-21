package zad1.Main_Server;

import zad1.GlobalLogger;
import zad1.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    private final String host;
    private final int port;

    private Map<String, Integer> connectedLanguageServers = new HashMap<>();

    public MainServer(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        MainServer mainServer = new MainServer("localhost", 20100);
        mainServer.start();
    }

    public void start() {

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }

    }

    private void handleClient(Socket clientSocket){
        GlobalLogger.getLogger().info("MainServ - serving a client: " + clientSocket.getLocalAddress() + ", " + clientSocket.getLocalPort());
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            Request request = (Request) in.readObject();
            switch (request.getRequestType()){
                case CLIENT -> serveClientRequest(request);
                case LANGUAGE_SERVER -> serveLanguageRequest(request);
            }

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    private void serveClientRequest(Request request) {
        //TODO
    }

    private void serveLanguageRequest(Request langServReq){
        connectedLanguageServers.put(langServReq.getLanguageCode(), langServReq.getServerPort());
        GlobalLogger.getLogger().info("MainServ - Language server connected: " + langServReq.getLanguageCode());
    }

}
