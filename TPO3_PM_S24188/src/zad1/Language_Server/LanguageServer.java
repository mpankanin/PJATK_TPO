package zad1.Language_Server;

import zad1.Language_Server.Languages.LanguageEnglish;
import zad1.Language_Server.Languages.LanguagePack;
import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.RequestType;
import zad1.Util.Response;
import zad1.Util.ResponseCode;

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
            GlobalLogger.getLogger().info("Connected to main server: " + host + ", " + serverPort);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            GlobalLogger.getLogger().info("Sending a request to attach language server to main server: " + host + ", " + serverPort);
            Request request = new Request(null, languagePack.getLanguageCode(), null, port, RequestType.LANGUAGE_SERVER);

            out.writeObject(request);
            out.close();
            GlobalLogger.getLogger().info("Request has been sent correctly to main server: " + host + ", " + serverPort);
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    public void start(){
        GlobalLogger.getLogger().info("Starting language server: " + languagePack.getLanguageCode() + ", " + port);
        connectToMainServer(host, serverPort);
        startedTime = LocalDateTime.now();

        try(ServerSocket serverSocket = new ServerSocket(port)) {
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
        GlobalLogger.getLogger().info("LangServ - serving a client: " + clientSocket.getInetAddress());
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            Request request = (Request) in.readObject();
            String foreignWord = getForeignWord(request.getSentenceToTranslate());
            ResponseCode responseCode = getResponseCode(foreignWord);

            Response response = new Response(foreignWord, null, responseCode, getServerInfo());
            sendResponse(response, request.getPort());

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("LangServ - client served correctly: " + clientSocket.getInetAddress());
    }

    private ResponseCode getResponseCode(String foreignWord) {
        return foreignWord == null ? ResponseCode.TRANSLATION_NOT_FOUND : ResponseCode.OK;
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
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("LangServ - response has been sent correctly: " + port);
    }

}
