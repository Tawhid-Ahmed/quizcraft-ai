package com.tawhid.quizcraft.quiz.dto;

import com.tawhid.quizcraft.quiz.entity.Option;
import com.tawhid.quizcraft.quiz.enums.ReviewStatus;
import lombok.Data;

@Data
public class AnswerDto {
    private Long id;
    private Long questionId;
    private String answerText;
    private Long selectedOptionId; // if applicable
    private Double score;
    private boolean aiGraded;
    private ReviewStatus reviewStatus;
    private String aiFeedback;

}
