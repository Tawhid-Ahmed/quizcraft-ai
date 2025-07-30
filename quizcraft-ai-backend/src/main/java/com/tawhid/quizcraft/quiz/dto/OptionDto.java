package com.tawhid.quizcraft.quiz.dto;

import lombok.Data;

@Data
public class OptionDto {
    private Long id;
    private String text;
    private boolean isCorrect;
}
