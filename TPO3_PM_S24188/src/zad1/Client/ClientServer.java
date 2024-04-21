package zad1.Client;

import zad1.GlobalLogger;
import zad1.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientServer {

    public static Response receiveResponse(final int port){
        Response response = null;

        try (ServerSocket serverSocket = new ServerSocket(port)){
            Socket socket = serverSocket.accept();
            ObjectInputStream out = new ObjectInputStream(socket.getInputStream());

            response = (Response) out.readObject();

            out.close();
            socket.close();
            return response;
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }

        return response;
    }

}
