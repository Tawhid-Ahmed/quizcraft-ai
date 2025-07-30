package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.dto.SubmissionDto;

import java.util.List;

public interface SubmissionService {
    SubmissionDto submitAnswer(SubmissionDto submissionDto);
    SubmissionDto getSubmissionById(Long id);
    List<SubmissionDto> getSubmissionsBYUserId(Long userId);
    List<SubmissionDto> getSubmissionsByQuizId(Long quizId);
}
