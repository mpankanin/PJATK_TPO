package zad1.Main_Server;

import zad1.GlobalLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private static final String HOST = "localhost";
    private static final int PORT = 20100;


    public void start() {

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }

    }

    private void handleClient(Socket clientSocket){










    }
}
