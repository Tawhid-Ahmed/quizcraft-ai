package com.tawhid.quizcraft.quiz.controller;

import com.tawhid.quizcraft.quiz.dto.QuizDto;
import com.tawhid.quizcraft.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizManagementController {

    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<QuizDto> createQuiz(@RequestBody QuizDto quizDto){
        return ResponseEntity.ok(quizService.createQuiz(quizDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<QuizDto>  updateQuiz(@PathVariable Long id, @RequestBody QuizDto quizDto){
        return ResponseEntity.ok(quizService.updateQuiz(id,quizDto));
    }
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<QuizDto> publishQuiz(@PathVariable Long id , @RequestParam boolean publish){
        return ResponseEntity.ok(quizService.togglePublishStatus(id,publish));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteQuiz(@PathVariable Long id){
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/all-quize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizDto>> getAllQuizzes(){
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }
    @GetMapping("/mine")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuizDto>> getMineQuizzes(){
        return ResponseEntity.ok(quizService.getQuizzesByCurrentTeacher());
    }




}
