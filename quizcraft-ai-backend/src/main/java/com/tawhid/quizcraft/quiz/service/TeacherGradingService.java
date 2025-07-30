package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.entity.Answer;

import java.util.List;

public interface TeacherGradingService {
    List<Answer> getDescriotiveAnswersNeedingGrading();
    Answer gradeDescriotiveAnswer(Long answerId, Double score);
}
