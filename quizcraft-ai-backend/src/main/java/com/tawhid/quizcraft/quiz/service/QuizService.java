package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.dto.QuizDto;

import java.util.List;

public interface QuizService {
    QuizDto createQuiz(QuizDto quizDto);
    QuizDto updateQuiz(Long id, QuizDto quizDto);
    QuizDto getQuizById(Long id);
    List<QuizDto> getAllQuizzes();
    List<QuizDto> getPublishedQuizzes();
    void deleteQuiz(Long id);
    QuizDto togglePublishStatus(Long id, boolean published);
    List<QuizDto> getQuizzesByCurrentTeacher();
}
