package com.tawhid.quizcraft.quiz.dto;

import com.tawhid.quizcraft.quiz.enums.QuestionType;
import lombok.Data;

import java.util.List;
@Data
public class QuestionDto {
    private Long id;
    private String content;
    private QuestionType type;
    private String explanation;
    private Integer marks;
    private List<OptionDto> options;


}
