package com.example.websocket.dtos;



import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    @JsonProperty("chatId")
    private String chatId;

    @JsonProperty("userIdentifier")
    private String userIdentifier;

    @JsonProperty("from")
    private String from;           // USER | BOT

    @JsonProperty("text")
    private String text;

    @JsonProperty("timestamp")
    private Long timestamp;

    // Constructori
    public ChatMessage() {}

    public ChatMessage(String chatId, String userIdentifier, String from, String text, Long timestamp) {
        this.chatId = chatId;
        this.userIdentifier = userIdentifier;
        this.from = from;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters și setters
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "chatId='" + chatId + '\'' +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", from='" + from + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}