package com.tawhid.quizcraft.quiz.service;


import com.tawhid.quizcraft.quiz.dto.AnswerDto;
import com.tawhid.quizcraft.quiz.entity.Answer;
import com.tawhid.quizcraft.quiz.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerReviewServiceImpl implements AnswerReviewService {
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AnswerDto> getPendingDescriptiveAnswers() {
        List<Answer> pendingAnswers = answerRepository
                .findByScoreIsNullAndAiGradedFalse();
        return pendingAnswers.stream()
                .map(answer -> modelMapper.map(answer, AnswerDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public AnswerDto gradeAnswer(Long answerId, Double score) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        answer.setScore(score);
        answer.setAiGraded(true);
        answerRepository.save(answer);
        return modelMapper.map(answer, AnswerDto.class);
    }

}
