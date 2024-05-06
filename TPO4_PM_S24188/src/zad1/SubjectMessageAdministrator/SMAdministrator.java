package zad1.SubjectMessageAdministrator;

import zad1.Util.GlobalLogger;
import zad1.Util.Request;
import zad1.Util.RequestType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class SMAdministrator {

    private SocketChannel channel = null;
    private final Charset charset;
    private final Set<String> topics;

    public SMAdministrator(String host, int serverPort) {
        start(new InetSocketAddress(host, serverPort));
        charset = Charset.defaultCharset();
        topics = new HashSet<>();
    }

    public static void main(String[] args) {
        new SMAdministrator("localhost", 20100);
    }


    private void start(InetSocketAddress inetAdr) {
        GlobalLogger.getLogger().info("[Admin] - connecting to the server");
        try {
            this.channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(inetAdr);
        } catch (IOException e) {
            GlobalLogger.getLogger().severe("[Admin] - " + e);
            System.exit(1);
        }
        GlobalLogger.getLogger().info("[Admin] - connected to the server");
    }

    public String requestAddTopic(String topic){
        if(topic == null){
            GlobalLogger.getLogger().severe("[Admin] - requestAddTopic - topic cannot be null");
            return "ERROR - requestRemoveTopic - provided topic is empty";
        }

        if(!topics.contains(topic)) {
            GlobalLogger.getLogger().info("[Admin] - adding topic: " + topic);
            Request request = new Request(RequestType.ADD_TOPIC, topic);
            Request.sendRequest(request, channel, charset, "[Admin]");
            topics.add(topic);
            GlobalLogger.getLogger().info("[Admin] - topic has been added: " + topic);
            return "OK - requestRemoveTopic - topic added successfully: " + topic;
        }else {
            GlobalLogger.getLogger().warning("[Admin] - provided topic already exists - operation add topic ignored");
            return "ERROR - requestRemoveTopic - provided topic doesn't exist - operation ignored";
        }
    }

    public String requestRemoveTopic(String topic){
        if(topic == null){
            GlobalLogger.getLogger().severe("[Admin] - requestRemoveTopic - topic cannot be null");
            return "ERROR - requestRemoveTopic - provided topic is empty";
        }

        if(topics.contains(topic)){
            GlobalLogger.getLogger().info("[Admin] - removing topic: " + topic);
            Request request = new Request(RequestType.REMOVE_TOPIC, topic);
            Request.sendRequest(request, channel, charset, "[Admin]");
            topics.remove(topic);
            GlobalLogger.getLogger().info("[Admin] - topic has been removed: " + topic);
            return "OK - requestRemoveTopic - topic removed successfully: " + topic;
        }else {
            GlobalLogger.getLogger().warning("[Admin] - provided topic doesn't exist - operation remove topic ignored");
            return "ERROR - requestRemoveTopic - provided topic doesn't exist - operation ignored";
        }
    }

    public String publishNews(String topic, String news){
        if(topic == null || news == null){
            GlobalLogger.getLogger().severe("[Admin] - publishNews - topic or news cannot be null");
            return "ERROR - requestRemoveTopic - provided topic is empty";
        }

        if(topics.contains(topic)){
            GlobalLogger.getLogger().info("[Admin] - publishing news: " + topic + ", " + news);
            Request request = new Request(RequestType.PUBLISH_NEWS, news);
            Request.sendRequest(request, channel, charset, "[Admin]");
            GlobalLogger.getLogger().info("[Admin] - news has been published: " + topic + ", " + news);
            return "OK - publishNews - " + topic + ", " + news;
        }else {
            GlobalLogger.getLogger().warning("[Admin] - publishNews - topic doesn't exist - operation ignored");
            return  "ERROR - publishNews - topic doesn't exist: " + topic;
        }
    }

}
