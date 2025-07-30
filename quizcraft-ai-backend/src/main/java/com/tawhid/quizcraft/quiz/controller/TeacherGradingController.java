package com.tawhid.quizcraft.quiz.controller;


import com.tawhid.quizcraft.quiz.entity.Answer;
import com.tawhid.quizcraft.quiz.service.TeacherGradingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/grading")
@RequiredArgsConstructor
public class TeacherGradingController {

    private final TeacherGradingService teacherGradingService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public List<Answer> getPendingAnswersNeedingGrading() {
        return teacherGradingService.getDescriotiveAnswersNeedingGrading();
    }
    @PostMapping("/grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Answer gradeAnswer(@RequestBody GradeRequest request) {
        return teacherGradingService.gradeDescriotiveAnswer(request.getAnswerId(), request.getScore());
    }
    @Data
    public static class GradeRequest {
        private Long answerId;
        private double score;
    }


}
