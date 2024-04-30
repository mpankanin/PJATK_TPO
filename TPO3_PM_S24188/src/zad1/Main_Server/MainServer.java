package zad1.Main_Server;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.Response;
import zad1.Util.ResponseCode;

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
                case CLIENT -> serveClientRequest(request);
                case LANGUAGE_SERVER -> serveLanguageRequest(request);
                case LANGUAGE_SERVER_REMOVE -> removeLanguageServer(request);
            }

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    private void serveClientRequest(final Request request) {
        if(!connectedLanguageServers.containsKey(request.getLanguageCode().toLowerCase())){
            //Bad request -> send error directly to the user
            GlobalLogger.getLogger().warning("MainServ - Couldn't find server with language code: " + request.getLanguageCode());
            Response badResponse = new Response(null, request.getPort(), ResponseCode.LANGUAGE_SERVER_NOT_FOUND, getServerInfo());
            sendResponseToClient(badResponse);
        }else {
            //OK request -> forward the request to the language server
            GlobalLogger.getLogger().info("MainServ - Requested language code server found, forwarding the request: " + request.getLanguageCode() + ", " + request.getSentenceToTranslate());
            sendRequestToLanguageServer(request);
        }
    }

    private void serveLanguageRequest(Request langServReq){
        connectedLanguageServers.put(langServReq.getLanguageCode().toLowerCase(), langServReq.getServerPort());
        GlobalLogger.getLogger().info("MainServ - Language server connected: " + langServReq.getLanguageCode());
    }

    private void removeLanguageServer(Request langServReq){
        if(connectedLanguageServers.containsKey(langServReq.getLanguageCode())){
            connectedLanguageServers.remove(langServReq.getLanguageCode().toLowerCase());
        }
        GlobalLogger.getLogger().info("MainServ - Language server disconnected: " + langServReq.getLanguageCode());
    }

    private String getServerInfo(){
        return "MainServer{host = " + host + ", port = " + port + ", started = " + startedTime.toString() + "}";
    }

    private void sendResponseToClient(final Response response){
        GlobalLogger.getLogger().info("MainServ - sending a response to: " + response.getTargetPort());
        try (Socket socket = new Socket(host, response.getTargetPort())){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(response);

            out.close();
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("MainServ - response has been sent correctly: " + response.getTargetPort());
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
