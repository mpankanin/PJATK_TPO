package zad1.SubjectMessageAdministrator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SMAdministratorGUI extends JFrame {
    private JTextField topicField;
    private JTextField newsField;
    private JTextField topicNewsField;
    private JButton sendRequestButton;
    private JButton publishNewsButton;
    private JComboBox<String> requestTypeComboBox;
    private JTextArea messageArea;
    private SMAdministrator smAdministrator;

    public SMAdministratorGUI(SMAdministrator smAdministrator) {
        this.smAdministrator = smAdministrator;

        setTitle("SMAdministrator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel topicPanel = new JPanel();
        JPanel newsPanel = new JPanel();

        topicField = new JTextField(20);
        sendRequestButton = new JButton("Send Request");

        newsField = new JTextField(20);
        topicNewsField = new JTextField(20);
        publishNewsButton = new JButton("Publish News");

        requestTypeComboBox = new JComboBox<>(new String[]{"Subscribe", "Unsubscribe"});
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        topicPanel.add(new JLabel("Topic:"));
        topicPanel.add(topicField);
        topicPanel.add(requestTypeComboBox);
        topicPanel.add(sendRequestButton);

        newsPanel.add(new JLabel("News:"));
        newsPanel.add(newsField);
        newsPanel.add(new JLabel("Topic:"));
        newsPanel.add(topicNewsField);
        newsPanel.add(publishNewsButton);

        mainPanel.add(topicPanel);
        mainPanel.add(newsPanel);

        add(mainPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        sendRequestButton.addActionListener((ActionEvent e) -> {
            String topic = topicField.getText();
            String requestType = (String) requestTypeComboBox.getSelectedItem();
            if ("Subscribe".equals(requestType)) {
                String output = smAdministrator.requestAddTopic(topic);
                displayMessage(output);
            } else if ("Unsubscribe".equals(requestType)) {
                String output = smAdministrator.requestRemoveTopic(topic);
                displayMessage(output);
            }
        });

        publishNewsButton.addActionListener((ActionEvent e) -> {
            String topic = topicNewsField.getText();
            String news = newsField.getText();
            String output = smAdministrator.publishNews(topic, news);
            displayMessage(output);
        });

        setVisible(true);
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }

}