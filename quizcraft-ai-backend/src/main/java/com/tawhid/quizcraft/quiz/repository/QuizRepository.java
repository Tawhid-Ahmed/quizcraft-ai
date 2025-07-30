package com.tawhid.quizcraft.quiz.repository;

import com.tawhid.quizcraft.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByPublishedTrueAndDeletedAtIsNull();
    List<Quiz> findByCreatorIdAndDeletedAtIsNull(Long teacherId);
    Optional<Quiz> findByIdAndPublishedTrueAndDeletedAtIsNull(Long id);
}
