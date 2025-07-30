package com.tawhid.quizcraft.ai.service;

public interface AiService {
    String generatedExplanation(String questionText, String userAnswer, String correctAnswer);
    double evaluateAnswer(String question, String userAnswer, String correctAnswer, int maxScore);
}
