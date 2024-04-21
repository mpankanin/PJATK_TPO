package zad1.Language_Server;

import zad1.GlobalLogger;
import zad1.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LanguageClient {

    private final String host;

    public LanguageClient(String host) {
        this.host = host;
    }

    public void sendResponse(final Response response, final int serverPort){

        try (Socket socket = new Socket(host, serverPort)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(response);
            out.close();
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

}
