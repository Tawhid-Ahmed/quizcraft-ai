package com.tawhid.quizcraft.quiz.controller;


import com.tawhid.quizcraft.quiz.dto.QuizDto;
import com.tawhid.quizcraft.quiz.dto.SubmissionDto;
import com.tawhid.quizcraft.quiz.service.QuizStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizStudentController {

    private final QuizStudentService quizStudentService;

    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<QuizDto>> listPublishedQuizzes() {
        return ResponseEntity.ok(quizStudentService.getAvailableQuizzes());
    }

    @GetMapping("/quizzes/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizDto> getQuizWithQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(quizStudentService.getQuizDetails(id));
    }
    @GetMapping("/submissions/mine")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionDto>> listMySubmissions() {
        return ResponseEntity.ok(quizStudentService.getMySubmissions());
    }
    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionDto> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(quizStudentService.getSubmissionById(id));
    }

}
