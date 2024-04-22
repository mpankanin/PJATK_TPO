package zad1.Main_Server;

import zad1.GlobalLogger;
import zad1.Request;
import zad1.Response;
import zad1.ResponseCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    private final String host;
    private final int port;

    private LocalDateTime startedTime;

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
        GlobalLogger.getLogger().info("Starting main server: " + host + ", " + port);
        startedTime = LocalDateTime.now();

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            GlobalLogger.getLogger().info("Main server started, listening client's request: " + host + ", " + port);
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
                case CLIENT -> serveClientRequest(request, clientSocket);
                case LANGUAGE_SERVER -> serveLanguageRequest(request);
            }

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    private void serveClientRequest(final Request request, final Socket clientSocket) {
        if(!connectedLanguageServers.containsKey(request.getLanguageCode())){
            //Bad request -> send error directly to the user
            Response badResponse = new Response(null, ResponseCode.LANGUAGE_SERVER_NOT_FOUND, getServerInfo());
            sendResponseToClient(badResponse, clientSocket);
        }else {
            //OK request -> forward the request to the language server
            sendRequestToLanguageServer(request);
        }
    }

    private void serveLanguageRequest(Request langServReq){
        connectedLanguageServers.put(langServReq.getLanguageCode(), langServReq.getServerPort());
        GlobalLogger.getLogger().info("MainServ - Language server connected: " + langServReq.getLanguageCode());
    }

    private String getServerInfo(){
        return "MainServer{host = " + host + ", port = " + port + ", started = " + startedTime.toString() + "}";
    }

    private void sendResponseToClient(final Response response, final Socket clientSocket){
        GlobalLogger.getLogger().info("MainServ - sending a response to: " + clientSocket.getLocalPort());
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())){
            out.writeObject(response);
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("MainServ - couldn't connect with a client: " + clientSocket.getLocalPort());
        }
        GlobalLogger.getLogger().info("MainServ - response has been sent correctly: " + clientSocket.getLocalPort());
    }

    private void sendRequestToLanguageServer(Request request) {
        GlobalLogger.getLogger().info("MainServ - sending a request to LangServ: " + request.getLanguageCode());
        int langPort = connectedLanguageServers.get(request.getLanguageCode());
        try (Socket socket = new Socket(host, langPort)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);

            out.close();
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("MainServ - couldn't connect with a LangServ: " + request.getLanguageCode());
        }
        GlobalLogger.getLogger().info("MainServ - response has been sent correctly to LangServ: " + request.getLanguageCode());
    }

}
