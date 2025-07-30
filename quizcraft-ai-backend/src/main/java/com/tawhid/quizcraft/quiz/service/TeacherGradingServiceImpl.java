package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.quiz.entity.Answer;
import com.tawhid.quizcraft.quiz.entity.Submission;
import com.tawhid.quizcraft.quiz.repository.AnswerRepository;
import com.tawhid.quizcraft.quiz.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherGradingServiceImpl implements TeacherGradingService{
        private final AnswerRepository answerRepository;
        private final SubmissionRepository submissionRepository;
        @Override
        public List<Answer> getDescriotiveAnswersNeedingGrading(){
            return answerRepository.findAll().stream()
                    .filter(answer -> answer.getAnswerText()!= null &&
                            !answer.isAiGraded() && answer.getScore() ==null)
                    .toList();

        }

        @Transactional
        @Override
        public  Answer gradeDescriotiveAnswer(Long answerId, Double score){
            Answer answer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new RuntimeException("Answer Not Found"));

            answer.setScore(score);
            answerRepository.save(answer);
            updateTotalScore(answer.getSubmission().getId());
            return answer;
        }


        private void updateTotalScore(Long submissionId){

            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission Not Found"));
            double scoreTotal =submission.getAnswers().stream()
                    .filter(answer -> answer.getScore() != null)
                    .mapToDouble(Answer::getScore)
                    .sum();

            submission.setScoreTotal(scoreTotal);
            submissionRepository.save(submission);
        }



}
