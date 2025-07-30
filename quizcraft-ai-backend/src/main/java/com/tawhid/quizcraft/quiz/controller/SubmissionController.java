package com.tawhid.quizcraft.quiz.controller;

import com.tawhid.quizcraft.quiz.dto.SubmissionDto;
import com.tawhid.quizcraft.quiz.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionDto> submitAnswer(@RequestBody SubmissionDto submissionDto){
        SubmissionDto result = submissionService.submitAnswer(submissionDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDto> getSubmissionById(@PathVariable Long id){
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(submissionService.getSubmissionsBYUserId(userId));
    }
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<SubmissionDto>> getSubmissionsByQuizId(@PathVariable Long quizId){
        return ResponseEntity.ok(submissionService.getSubmissionsByQuizId(quizId));
    }

}
