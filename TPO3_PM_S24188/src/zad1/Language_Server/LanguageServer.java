package zad1.Language_Server;

import zad1.GlobalLogger;
import zad1.LanguageServerRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class LanguageServer {

    private final int port;
    private final String languageCode;
    private Map<String, String> dictionary;

    public LanguageServer(String host, int port, String languageCode, Map<String, String> dictionary) {
        this.port = port;
        this.languageCode = languageCode;
        this.dictionary = dictionary;
    }

    public void addToDictionary(String word, String foreignWord){
        if(!dictionary.containsKey(word)) {
            dictionary.put(word, foreignWord);
        }
    }

    public void connectToMainServer(final String host, final int serverPort) {
        try (Socket socket = new Socket(host, serverPort)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            LanguageServerRequest request = new LanguageServerRequest(languageCode, port);

            out.writeObject(request);
            out.close();
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }


    }


}
