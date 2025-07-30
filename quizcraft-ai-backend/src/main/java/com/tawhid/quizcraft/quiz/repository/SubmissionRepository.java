package com.tawhid.quizcraft.quiz.repository;

import com.tawhid.quizcraft.quiz.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);
    List<Submission> findByQuizId(Long quizId);
}
