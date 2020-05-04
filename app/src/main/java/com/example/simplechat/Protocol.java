package com.example.simplechat;

import com.google.gson.Gson;

public class Protocol {
    public final static int USER_STATUS = 1;
    public final static int MESSAGE = 2;
    public final static int USER_NAME = 3;

    static class User {
        private long id;
        private String name;

        public User() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class UserStatus {
        private boolean connected;
        private User user;

        public UserStatus() {
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    static class UserName {
        private String name;

        public UserName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class Message {
        final static int GROUP_CHAT = 1;
        private long sender;
        private String encodedText;
        private long receiver = GROUP_CHAT;

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }
    }

    public static int getType(String json) {
        if (json == null || json.length() == 0) {
            return -1;
        }

        return Integer.parseInt(json.substring(0, 1));
    }

    public static String packName(UserName name) {
        Gson g = new Gson();

        return USER_NAME + g.toJson(name);
    }

    public static UserName unpackName(String json) {
        Gson g = new Gson();

        return g.fromJson(json.substring(1), UserName.class);
    }

    public static String packMessage(Message message) {
        Gson g = new Gson();

        return MESSAGE + g.toJson(message);
    }

    public static Message unpackMessage(String json) {
        Gson g = new Gson();

        return g.fromJson(json.substring(1), Message.class);
    }

    public static String packUserStatus(UserStatus userstatus) {
        Gson g = new Gson();

        return MESSAGE + g.toJson(userstatus);
    }

    public static UserStatus unpackUserStatus(String json) {
        Gson g = new Gson();

        return g.fromJson(json.substring(1), UserStatus.class);
    }
}
