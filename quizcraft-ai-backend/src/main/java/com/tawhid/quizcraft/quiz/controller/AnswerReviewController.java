package com.tawhid.quizcraft.quiz.controller;

import com.tawhid.quizcraft.quiz.dto.AnswerDto;
import com.tawhid.quizcraft.quiz.service.AnswerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class AnswerReviewController {

    private final AnswerReviewService answerReviewService;

    @GetMapping("/pending")
    public ResponseEntity<List<AnswerDto>> getPendingDescriptiveAnswers() {
        return ResponseEntity.ok(answerReviewService.getPendingDescriptiveAnswers());
    }

    @PostMapping("/{answerId}/grade")
    public ResponseEntity<AnswerDto> gradeAnswer(@PathVariable Long answerId, @RequestParam Double score) {
        return ResponseEntity.ok(answerReviewService.gradeAnswer(answerId,score));
    }

}
