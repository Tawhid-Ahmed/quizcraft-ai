package com.tawhid.quizcraft.ai.service;

import org.springframework.stereotype.Service;

@Service
public class MockAiServiceImpl implements AiService {
    @Override
    public String generatedExplanation(String questionText, String userAnswer, String correctAnswer) {
        return String.format("You answered \"%s\", but the correct answer is \"%s\". Please review the question: %s",
                userAnswer, correctAnswer, questionText);
    }

    @Override
    public double evaluateAnswer(String question, String userAnswer, String correctAnswer, int maxScore) {
        // Simple mock implementation
        boolean isCorrect = userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer);
        return isCorrect ? maxScore : 0.0;
    }
}
