package com.tawhid.quizcraft.quiz.controller;

import com.tawhid.quizcraft.quiz.entity.Answer;
import com.tawhid.quizcraft.quiz.repository.AnswerRepository;
import com.tawhid.quizcraft.quiz.service.AiGradingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/grading")
@RequiredArgsConstructor
public class AiGradingController {
    private final AiGradingService aiGradingService;
    private final AnswerRepository answerRepository;
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Answer> getPendingAiGradingAnswers() {
        return aiGradingService.getUngradedDescriptiveAnswers();
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> gradeWithAi(@RequestBody GradeRequest gradeRequest) {
        Answer answer = answerRepository.findById(gradeRequest.getAnswerId())
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        boolean success = aiGradingService.gradeDescriptiveAnswer(answer);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("answerId", gradeRequest.getAnswerId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/grade-all")
    public ResponseEntity<Map<String, Object>> gradeAllUngradedDescriptiveAnswers() {
        List <Answer> ungradedAnswers = answerRepository.findUngradedDescriptiveAnswers();
         int total =  ungradedAnswers.size();
         int graded = 0;

         for (Answer answer : ungradedAnswers) {
             boolean success = aiGradingService.gradeDescriptiveAnswer(answer);
             if (success) {graded++;}
         }
         Map<String, Object> result = new HashMap<>();
        result.put("totalFound", total);
        result.put("successfullyGraded", graded);

        return ResponseEntity.ok(result);


    }

    @Data
    public static class GradeRequest {
        private Long answerId;
    }

}
