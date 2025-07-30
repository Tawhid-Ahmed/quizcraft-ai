package com.tawhid.quizcraft.quiz.dto;

import com.tawhid.quizcraft.auth.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class SubmissionDto {
    private Long id;
    private User userId;
    private Long quizId;
    private ZonedDateTime submittedAt;
    private Double scoreTotal;
    private List<AnswerDto> answers;
}
