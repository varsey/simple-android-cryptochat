package com.example.simplechat;

import android.util.Log;
import android.util.Pair;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Server {
    private WebSocketClient client;
    private Map<Long, String> names = new ConcurrentHashMap<>();
    private Consumer<Pair<String, String>> onMessageReceived;
    private Consumer<Pair<String, Integer>> onUserEnterToChat;
    private Consumer<Pair<String, Integer>> onUserLeaveChat;

    public Server(
            Consumer<Pair<String, String>> onMessageReceived,
            Consumer<Pair<String, Integer>> onUserEnterToChat,
            Consumer<Pair<String, Integer>> onUserLeaveChat
    ) {
        this.onMessageReceived = onMessageReceived;
        this.onUserEnterToChat = onUserEnterToChat;
        this.onUserLeaveChat = onUserLeaveChat;
    }

    public void connect() throws URISyntaxException {
        URI address = null;

        try {
            address = new URI("wd://79.137.174.97:8881"); //35.214.1.221
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connected");
            }

            @Override
            public void onMessage(String message) {
                Log.i("SERVER", "Message: " + message);
                int type = Protocol.getType(message);

                if (type == Protocol.MESSAGE) {
                    displayIncoming(Protocol.unpackMessage(message));
                }
                if (type == Protocol.USER_STATUS) {
                   updateStatus(Protocol.unpackUserStatus(message));
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER", "Close, reason: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("SERVER", "Error: " + ex.getMessage());
            }
        };

        client.connect();
    }

    public void sendName(String name) {
        Protocol.UserName userName = new Protocol.UserName(name);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packName(userName));
        }
    }

    public void sendMessage(String msg) {
        try {
            msg = Crypto.encrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message message = new Protocol.Message(msg);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packMessage(message));
        }
    }

    private void updateStatus(Protocol.UserStatus status) {
        Protocol.User user = status.getUser();
        if (status.isConnected()) {
            names.put(user.getId(), user.getName());
            onUserEnterToChat.accept(new Pair<String, Integer>(user.getName(), names.size()));
        } else {
            names.remove(user.getId());
            onUserLeaveChat.accept(new Pair<String, Integer>(user.getName(), names.size()));
        }
    }

    private void displayIncoming(Protocol.Message message) {
        String name = names.get(message.getSender());
        String msg = null;
        try {
            msg = Crypto.decrypt(message.getEncodedText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (name == null) {
            name = "Unnamed";
        }

        onMessageReceived.accept(new Pair<String, String>(name, msg));
    }
}
