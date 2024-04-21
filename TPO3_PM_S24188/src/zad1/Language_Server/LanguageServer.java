package zad1.Language_Server;

import zad1.GlobalLogger;
import zad1.Request;
import zad1.RequestType;
import zad1.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class LanguageServer {

    private final String host;
    private final int port;
    private final int serverPort;
    private final LanguagePack languagePack;

    private LocalDateTime startedTime;

    public LanguageServer(String host, int port, int serverPort, LanguagePack languagePack) {
        this.host = host;
        this.port = port;
        this.serverPort = serverPort;
        this.languagePack = languagePack;
    }

    public static void main(String[] args) {
        //Load the language pack
        LanguagePack languagePack = new LanguageEnglish();

        //Start the server
        LanguageServer languageServer = new LanguageServer("localhost", 20201, 20100, languagePack);
        languageServer.start();
    }

    public void connectToMainServer(final String host, final int serverPort) {
        GlobalLogger.getLogger().info("Connecting to main server: " + host + ", " + serverPort);
        try (Socket socket = new Socket(host, serverPort)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            Request request = new Request(null, languagePack.getLanguageCode(), null, port, RequestType.LANGUAGE_SERVER);

            out.writeObject(request);
            out.close();
            GlobalLogger.getLogger().info("Connected to main server: " + host + ", " + serverPort);
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    public void start(){
        GlobalLogger.getLogger().info("Starting language server: " + languagePack.getLanguageCode() + ", " + port);
        connectToMainServer(host, serverPort);
        startedTime = LocalDateTime.now();

        try(ServerSocket serverSocket = new ServerSocket(serverPort)) {
            GlobalLogger.getLogger().info("Language server started, listening client's request: " + languagePack.getLanguageCode() + ", " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    private void handleClient(Socket clientSocket) {
        GlobalLogger.getLogger().info("LangServ - serving a client: " + clientSocket.getLocalAddress() + ", " + clientSocket.getLocalPort());
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            Request request = (Request) in.readObject();
            String foreignWord = getForeignWord(request.getSentenceToTranslate());

            Response response = new Response(foreignWord, getServerInfo());
            sendResponse(response, request.getPort());

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("LangServ - client served correctly: " + clientSocket.getLocalAddress() + ", " + clientSocket.getLocalAddress());
    }

    private String getForeignWord(String word){
        String foreignWord = null;
        if(languagePack.getDictionary().containsKey(word)){
            foreignWord = languagePack.getDictionary().get(word);
        }else {
            GlobalLogger.getLogger().severe("LangServ - couldn't find translation for the word: " + word);
        }
        return foreignWord;
    }

    private String getServerInfo(){
        return "LanguageServer{" + languagePack.getLanguageCode() + ", " + port + ", " + startedTime + "}";
    }

    private void sendResponse(final Response response, final int port){
        GlobalLogger.getLogger().info("LangServ - sending a response to: " + port);
        try (Socket socket = new Socket(host, port)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(response);

            out.close();
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("LangServ - couldn't connect with a client: " + port);
        }
        GlobalLogger.getLogger().info("LangServ - response has been sent correctly: " + port);
    }

}
