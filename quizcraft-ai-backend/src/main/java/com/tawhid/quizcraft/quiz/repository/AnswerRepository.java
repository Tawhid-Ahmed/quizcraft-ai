package com.tawhid.quizcraft.quiz.repository;

import com.tawhid.quizcraft.quiz.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findBySubmissionId(Long submissionId);
    List<Answer> findByScoreIsNullAndAiGradedFalse();


    @Query("SELECT a FROM Answer a " +
            "JOIN a.question q " +
            "WHERE q.type = 'DESCRIPTIVE' " +
            "AND a.score IS NULL AND a.aiGraded = false")
    List<Answer> findUngradedDescriptiveAnswers();

}
