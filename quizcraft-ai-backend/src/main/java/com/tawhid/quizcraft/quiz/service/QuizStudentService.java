package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.dto.QuizDto;
import com.tawhid.quizcraft.quiz.dto.SubmissionDto;

import java.util.List;

public interface QuizStudentService {
    List<QuizDto> getAvailableQuizzes();
    QuizDto getQuizDetails(Long quizId);
    SubmissionDto submitQuiz(Long quizId,SubmissionDto submissionDto);
    List<SubmissionDto> getMySubmissions();
    SubmissionDto getSubmissionById(Long SubmissionId);

}
