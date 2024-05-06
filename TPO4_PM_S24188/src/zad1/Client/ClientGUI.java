package zad1.Client;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JComboBox<String> requestTypeComboBox;
    private Client client;

    public ClientGUI(Client client) {
        this.client = client;

        setTitle("Client");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        inputField = new JTextField(30);
        sendButton = new JButton("Send");
        requestTypeComboBox = new JComboBox<>(new String[]{"Subscribe", "Unsubscribe"});
        bottomPanel.add(inputField);
        bottomPanel.add(requestTypeComboBox);
        bottomPanel.add(sendButton);

        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            inputField.setText("");
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            if ("Subscribe".equals(requestType)) {
                client.requestSubscribe(text);
            } else if ("Unsubscribe".equals(requestType)) {
                client.requestUnsubscribe(text);
            }
        });

        setVisible(true);
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
}