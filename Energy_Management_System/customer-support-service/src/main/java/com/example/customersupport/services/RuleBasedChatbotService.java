package com.example.customersupport.services;

import com.example.customersupport.dtos.ChatRequestMessage;
import com.example.customersupport.dtos.ChatResponseMessage;
import com.example.customersupport.repositories.InMemoryChatLogRepository;
import com.example.customersupport.entities.ChatMessage;
import com.example.customersupport.services.rules.ChatRule;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleBasedChatbotService {

    private final List<ChatRule> rules;
    private final InMemoryChatLogRepository chatLog;
    private final com.example.customersupport.services.rules.GeminiAiService geminiAiService;

    // ✅ Rate limiter: max 5 AI requests per user per minute
    private final Map<String, UserRateLimit> rateLimits = new ConcurrentHashMap<>();
    private static final int MAX_AI_REQUESTS_PER_MINUTE = 5;

    public RuleBasedChatbotService(List<ChatRule> rules, InMemoryChatLogRepository chatLog,
                                   com.example.customersupport.services.rules.GeminiAiService geminiAiService) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(ChatRule::priority).reversed())
                .toList();
        this.chatLog = chatLog;
        this.geminiAiService = geminiAiService;
    }

    public ChatResponseMessage handle(ChatRequestMessage req) {
        Instant ts = (req.getTimestamp() != null)
                ? Instant.ofEpochMilli(req.getTimestamp())
                : Instant.now();

        ChatContext ctx = new ChatContext(req.getUserIdentifier(), req.getText(), ts);

        chatLog.append(new ChatMessage(
                req.getUserIdentifier(), req.getText(), ts, false));

        String lowerText = req.getText().toLowerCase().trim();
        boolean forceAI = lowerText.startsWith("ai:") ||
                lowerText.startsWith("ask:") ||
                lowerText.contains("gemini");

        // Încearcă regulile mai întâi (dacă nu e forțat AI)
        if (!forceAI) {
            for (ChatRule rule : rules) {
                if (rule.matches(ctx)) {
                    ChatResponseMessage resp = rule.apply(ctx);
                    if (resp.getTimestamp() == null) resp.setTimestamp(Instant.now());
                    if (resp.getUserIdentifier() == null) resp.setUserIdentifier(req.getUserIdentifier());

                    chatLog.append(new ChatMessage(
                            req.getUserIdentifier(), resp.getReply(), resp.getTimestamp(), true));
                    return resp;
                }
            }
        }

        // ✅ Verifică rate limit ÎNAINTE de a apela Gemini
        if (!checkRateLimit(req.getUserIdentifier())) {
            String errorMsg = "⏱️ You're asking too many questions! Please wait a minute before trying AI again. " +
                    "In the meantime, try 'help' to see what I can answer instantly.";

            ChatResponseMessage rateLimitResp = new ChatResponseMessage(
                    req.getUserIdentifier(),
                    errorMsg,
                    Instant.now()
            );

            chatLog.append(new ChatMessage(
                    req.getUserIdentifier(), rateLimitResp.getReply(), rateLimitResp.getTimestamp(), true
            ));

            return rateLimitResp;
        }

        // Apelează Gemini AI
        String aiReply = geminiAiService.generateReply(
                req.getUserIdentifier(),
                req.getText().replaceFirst("^(ai:|ask:|gemini)", "").trim(),
                chatLog.getRecent(req.getUserIdentifier())
        );

        ChatResponseMessage aiResp = new ChatResponseMessage(
                req.getUserIdentifier(),
                aiReply,
                Instant.now()
        );

        chatLog.append(new ChatMessage(
                req.getUserIdentifier(), aiResp.getReply(), aiResp.getTimestamp(), true
        ));

        return aiResp;
    }

    // ✅ Rate limit checker
    private boolean checkRateLimit(String userId) {
        UserRateLimit limit = rateLimits.computeIfAbsent(userId, k -> new UserRateLimit());
        return limit.allowRequest();
    }

    public List<String> getRuleNamesInOrder() {
        return rules.stream().map(ChatRule::name).toList();
    }

    // ✅ Inner class pentru rate limiting
    private static class UserRateLimit {
        private long windowStart = System.currentTimeMillis();
        private int requestCount = 0;

        synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();

            // Reset window după 60 secunde
            if (now - windowStart > 60_000) {
                windowStart = now;
                requestCount = 0;
            }

            if (requestCount >= MAX_AI_REQUESTS_PER_MINUTE) {
                return false; // Rate limit exceeded
            }

            requestCount++;
            return true;
        }
    }
}