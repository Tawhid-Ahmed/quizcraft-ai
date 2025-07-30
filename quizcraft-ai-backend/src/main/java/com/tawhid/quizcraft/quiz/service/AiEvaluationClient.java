package com.tawhid.quizcraft.quiz.service;

import org.springframework.stereotype.Component;

@Component
public class AiEvaluationClient {
    public double evaluateAnswer(String question, String answerText, int maxScore) {
        // Simulated scoring logic – replace this with your LLM call
        if (answerText ==null || answerText.trim().isEmpty()) return 0;
        // Replace below with actual AI evaluation logic (e.g., call Ollama or API)
        double similarity = Math.min(1.0, answerText.length() /(double) (question.length()+50));
        return Math.round(similarity * maxScore * 10.0) / 10.0;
    }
}
