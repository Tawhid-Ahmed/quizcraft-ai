package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.dto.AnswerDto;

import java.util.List;

public interface AnswerReviewService {
    List<AnswerDto> getPendingDescriptiveAnswers();
    AnswerDto gradeAnswer(Long answerId, Double score);
}
