package com.tawhid.quizcraft.ai.service;

import com.tawhid.quizcraft.ai.model.OllamaRequest;
import com.tawhid.quizcraft.ai.model.OllamaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaAiService implements AiService {
    
    private final RestTemplate restTemplate;
    
    @Value("${ollama.api.url:http://localhost:11434/api/generate}")
    private String ollamaApiUrl;
    
    @Value("${ollama.model.name:mistral}")
    private String modelName;

    @Override
    public String generatedExplanation(String question, String userAnswer, String correctAnswer) {
        String prompt = constructPrompt(question, userAnswer, correctAnswer);
        OllamaRequest request = new OllamaRequest(modelName, prompt);
        
        try {
            log.debug("Sending explanation request to Ollama API for question: {}", question);
            OllamaResponse response = restTemplate.postForObject(ollamaApiUrl, request, OllamaResponse.class);
            if (response != null) {
                log.debug("Successfully received explanation from Ollama API");
                return response.getResponse();
            }
            log.warn("Received null response from Ollama API");
            return getFallbackResponse();
        } catch (Exception e) {
            log.error("Error while generating explanation: {}", e.getMessage(), e);
            return getFallbackResponse();
        }
    }

    @Override
    public double evaluateAnswer(String question, String userAnswer, String correctAnswer, int maxScore) {
        String prompt = constructEvaluationPrompt(question, userAnswer, correctAnswer);
        OllamaRequest request = new OllamaRequest(modelName, prompt);
        
        try {
            log.debug("Sending evaluation request to Ollama API for question: {}", question);
            OllamaResponse response = restTemplate.postForObject(ollamaApiUrl, request, OllamaResponse.class);
            if (response != null) {
                log.debug("Successfully received evaluation from Ollama API");
                return parseScore(response.getResponse(), maxScore);
            }
            log.warn("Received null response from Ollama API");
        } catch (Exception e) {
            log.error("Error while evaluating answer: {}", e.getMessage(), e);
        }
        return 0.0; // Default score if evaluation fails
    }

    private String constructPrompt(String question, String userAnswer, String correctAnswer) {
        return String.format("""
            Act as an educational assistant. Given:
            Question: %s
            Student's Answer: %s
            Correct Answer: %s
            
            Provide a helpful, encouraging explanation of why the answer was incorrect and guide the student toward understanding. 
            Keep the explanation clear, concise, and focused on the key concepts.
            """, question, userAnswer, correctAnswer);
    }

    private String constructEvaluationPrompt(String question, String userAnswer, String correctAnswer) {
        return String.format("""
            Act as an educational evaluator. Given:
            Question: %s
            Student's Answer: %s
            Correct Answer: %s
            
            Evaluate the student's answer and return a score between 0 and 1, where:
            0 = completely incorrect
            1 = completely correct
            Provide only the numeric score.
            """, question, userAnswer, correctAnswer);
    }

    private String getFallbackResponse() {
        return "I apologize, but I'm unable to provide a detailed explanation at the moment. " +
               "Please review the correct answer and consult with your teacher if you need clarification.";
    }

    private double parseScore(String aiResponse, int maxScore) {
        try {
            // Extract numeric value from AI response
            String numeric = aiResponse.replaceAll("[^0-9.]", "");
            double score = Double.parseDouble(numeric);
            // Normalize between 0 and maxScore
            return Math.min(Math.max(score, 0), 1) * maxScore;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
