package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.entity.Answer;
import com.tawhid.quizcraft.quiz.entity.Question;
import com.tawhid.quizcraft.quiz.entity.Submission;
import com.tawhid.quizcraft.quiz.repository.AnswerRepository;
import com.tawhid.quizcraft.quiz.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiGradingService {
    private final AnswerRepository answerRepository;
    private final SubmissionRepository submissionRepository;
    private  final AiEvaluationClient aiEvaluationClient;

    public List<Answer> getUngradedDescriptiveAnswers(){
        return answerRepository.findAll().stream()
                .filter(answer -> answer.getAnswerText()!=null &&
                        !answer.isAiGraded() &&
                        answer.getScore()== null)
                .toList();
    }

    @Transactional
    public boolean gradeDescriptiveAnswer(Answer answer){

        Question question = answer.getQuestion();
        if (question == null || question.getType() == null || answer.getAnswerText() == null || answer.getScore() == null){
            return false;
        }
        try {
            double score = aiEvaluationClient.evaluateAnswer(
                    question.getContent(),
                    answer.getAnswerText(),
                    question.getMarks()
            );
            answer.setScore(score);
            answer.setAiGraded(true);
            answerRepository.save(answer);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    private void updateTotalScore(Long submissionId){
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(()->new RuntimeException("submission not found"));

        double total = submission.getAnswers().stream()
                .filter(answer -> answer.getScore()!=null)
                .mapToDouble(Answer::getScore)
                .sum();
        submission.setScoreTotal(total);
        submissionRepository.save(submission);
    }







}
