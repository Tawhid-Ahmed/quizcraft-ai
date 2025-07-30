package com.tawhid.quizcraft.quiz.dto;

import com.tawhid.quizcraft.auth.entity.User;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class QuizDto {
    private Long id;
    private String title;
    private String description;
    private String language;
    private boolean published;
    private User creator;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<QuestionDto> questions;
}
