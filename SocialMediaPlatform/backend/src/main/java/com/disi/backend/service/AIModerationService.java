package com.disi.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AIModerationService {

    private static final Logger log = LoggerFactory.getLogger(AIModerationService.class);

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
        "HATE_SPEECH", List.of(
            // English
            "hate", "racist", "racism", "sexist", "homophobic", "nazi", "fascist",
            "bigot", "slur", "discrimination", "xenophobia",
            // Romanian
            "rasist", "rasism", "fascist", "discriminare", "xenofobie", "ura",
            "homofob", "intolerant", "segregare"
        ),
        "VULGAR_LANGUAGE", List.of(
            // English
            "fuck", "shit", "ass", "bitch", "cunt", "dick", "bastard",
            "damn", "hell", "crap", "piss", "cock", "whore",
            // Romanian
            "pula", "pizda", "muie", "cacat", "futut", "futu-ti", "dracu",
            "nenorocit", "curva", "fut", "bulangiu", "cacat"
        ),
        "HARASSMENT", List.of(
            // English
            "kill yourself", "kys", "die", "threat", "stalk", "harass",
            "abuse", "bully", "hurt you", "loser", "idiot", "stupid", "moron", "ugly", "worthless",
            // Romanian
            "omoara-te", "du-te dracului", "esti prost", "idiot", "cretin",
            "imbecil", "handicapat", "las", "fricos", "urat", "groaznic",
            "dispari", "te omor", "te gasesc", "bai proasto", "du-te dracu de prost "
        ),
        "SPAM", List.of(
            // English
            "click here", "buy now", "free money", "winner", "prize",
            "casino", "gambling", "earn money fast", "work from home",
            "100% free", "limited offer", "act now",
            // Romanian
            "apasa aici", "castiga bani", "castigator", "premiu",
            "pacanele", "pariuri", "castiga rapid", "lucreaza acasa",
            "oferta limitata", "gratuit 100%", "inscrie-te acum"
        )
    );

    private static final double THRESHOLD = 0.3;

    public record ModerationResult(boolean isFlagged, String category, String reason, double confidence) {}

    public ModerationResult analyze(String content) {
        if (content == null || content.isBlank()) {
            return new ModerationResult(false, null, null, 0.0);
        }

        String lowerContent = content.toLowerCase();
        String topCategory = null;
        int topMatches = 0;
        int totalWords = lowerContent.split("\\s+").length;

        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            int matches = (int) keywords.stream()
                    .filter(lowerContent::contains)
                    .count();

            if (matches > topMatches) {
                topMatches = matches;
                topCategory = category;
            }
        }

        if (topCategory == null || topMatches == 0) {
            return new ModerationResult(false, null, null, 0.0);
        }

        double confidence = Math.min(1.0, (double) topMatches / Math.max(1, totalWords / 5.0));

        if (confidence < THRESHOLD) {
            return new ModerationResult(false, null, null, confidence);
        }

        String reason = buildReason(topCategory, topMatches);
        log.info("Content flagged - category: {}, confidence: {}", topCategory, confidence);

        return new ModerationResult(true, topCategory, reason, confidence);
    }

    private String buildReason(String category, int matches) {
        return switch (category) {
            case "HATE_SPEECH" -> "Content contains hate speech or discriminatory language (" + matches + " indicator(s) detected)";
            case "VULGAR_LANGUAGE" -> "Content contains vulgar or offensive language (" + matches + " indicator(s) detected)";
            case "HARASSMENT" -> "Content contains harassment or threatening language (" + matches + " indicator(s) detected)";
            case "SPAM" -> "Content appears to be spam or promotional material (" + matches + " indicator(s) detected)";
            default -> "Content flagged for review (" + matches + " indicator(s) detected)";
        };
    }
}
