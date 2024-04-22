package zad1.Client;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.Response;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    private final String host;
    private final Integer port;
    private final JTextArea responseArea;

    public Client(final String host, final Integer port, final JTextArea responseArea) {
        this.host = host;
        this.port = port;
        this.responseArea = responseArea;
    }

    public void sendRequest(final Request request, final int port) {
        GlobalLogger.getLogger().info("Client - sending a request to: " + port);
        try (Socket socket = new Socket(host, port)){
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);

            out.close();
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("Client - request has been sent correctly: " + port);
    }

    void startClientServer(){
        GlobalLogger.getLogger().info("Starting client server: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            GlobalLogger.getLogger().info("Client server started, listening client's request: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
    }

    private void handleClient(Socket clientSocket) {
        GlobalLogger.getLogger().info("Client - processing a response from: " + clientSocket.getInetAddress());
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            Response response = (Response) in.readObject();

            displayResponse(responseArea, response.toString());

            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            GlobalLogger.getLogger().severe(ex.toString());
        }
        GlobalLogger.getLogger().info("Client - response processed correctly from: " + clientSocket.getInetAddress());
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void displayResponse(final JTextArea responseArea, final String response) {
        SwingUtilities.invokeLater(() -> responseArea.append(response + '\n'));
    }

}
