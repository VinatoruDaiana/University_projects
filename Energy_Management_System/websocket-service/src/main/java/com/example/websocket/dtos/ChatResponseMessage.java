package com.example.websocket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatResponseMessage {
    @JsonProperty("userIdentifier")
    private String userIdentifier;

    @JsonProperty("reply")
    private String reply;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("suggestions")
    private List<String> suggestions = new ArrayList<>();

    public ChatResponseMessage() {}

    public ChatResponseMessage(String userIdentifier, String reply, Instant timestamp) {
        this.userIdentifier = userIdentifier;
        this.reply = reply;
        this.timestamp = timestamp;
    }

    // Getters și setters
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    @Override
    public String toString() {
        return "ChatResponseMessage{" +
                "userIdentifier='" + userIdentifier + '\'' +
                ", reply='" + reply + '\'' +
                ", timestamp=" + timestamp +
                ", suggestions=" + suggestions +
                '}';
    }
}