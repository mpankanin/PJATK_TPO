package zad1.Client;

import zad1.Util.Request;
import zad1.Util.RequestType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {

    private Client client;
    private JTextField requestTextField;
    private JTextField requestLangCodeField;
    private JTextField requestPortField;


    public static void main(String[] args) {
        ClientGUI clientGUI = new ClientGUI();
        clientGUI.start();
    }

    private void start(){
        JTextArea responseArea = new JTextArea();
        responseArea.setEditable(false);

        client = new Client("localhost", 20300, responseArea);
        new Thread(() -> client.startClientServer()).start();

        JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        JScrollPane scrollPane = new JScrollPane(responseArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel requestTextLabel = new JLabel("Enter word:");
        requestTextField = new JTextField(20);
        JLabel requestLangCodeLabel = new JLabel("Enter language code:");
        requestLangCodeField = new JTextField(5);
        JLabel requestPortLabel = new JLabel("Server port:");
        requestPortField = new JTextField(5);
        requestPortField.setText("20100");
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        // Add labels and fields to the panel
        panel.add(requestTextLabel);
        panel.add(requestTextField);
        panel.add(requestLangCodeLabel);
        panel.add(requestLangCodeField);
        panel.add(requestPortLabel);
        panel.add(requestPortField);
        panel.add(new JLabel()); // Empty label for alignment
        panel.add(sendButton);

        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String requestText = requestTextField.getText().toLowerCase();
            String requestCode = requestLangCodeField.getText().toLowerCase();
            String requestPort = requestPortField.getText().toLowerCase();
            if (!requestText.isEmpty() && !requestCode.isEmpty() && !requestPort.isEmpty()) {
                Request request = new Request(requestText, requestCode, client.getPort(), null, RequestType.CLIENT);
                client.sendRequest(request, Integer.parseInt(requestPort));
                requestTextField.setText("");
            }
        }
    }

}
