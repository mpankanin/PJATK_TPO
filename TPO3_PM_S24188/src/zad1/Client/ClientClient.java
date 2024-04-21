package zad1.Client;

import zad1.GlobalLogger;
import zad1.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientClient {

    private static final String HOST = "localhost";
    private static final int SERVER_PORT = 20100;

    public static void sendRequest(final Request request) {
        try (Socket socket = new Socket(HOST, SERVER_PORT)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);
            out.close();
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

}
