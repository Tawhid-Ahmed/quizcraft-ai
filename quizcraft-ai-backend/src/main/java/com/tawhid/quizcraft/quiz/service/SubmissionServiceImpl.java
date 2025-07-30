package com.tawhid.quizcraft.quiz.service;


import com.tawhid.quizcraft.ai.service.AiService;
import com.tawhid.quizcraft.quiz.dto.AnswerDto;
import com.tawhid.quizcraft.quiz.dto.SubmissionDto;
import com.tawhid.quizcraft.quiz.entity.*;
import com.tawhid.quizcraft.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;
    private final AiService aiService;


    @Override
    public SubmissionDto submitAnswer(SubmissionDto submissionDto) {
        Quiz quiz = quizRepository.findById(submissionDto.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz Not Found"));

        Submission submission = new Submission();
        submission.setUserId(submissionDto.getUserId());
        submission.setQuiz(quiz);
        submission.setSubmittedAt(ZonedDateTime.now());

            double scoreTotal = 0.0;       
            List<Answer> answers = new ArrayList<>();
        for (AnswerDto answerDto : submissionDto.getAnswers()) {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question Not Found"));
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setAnswerText(answerDto.getAnswerText());
            if (answerDto.getSelectedOptionId() != null) {
                Option selectedOption = optionRepository.findById(answerDto.getSelectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Selected option not found"));
                answer.setSelectedOptionId(selectedOption);
            }
            answer.setSubmission(submission);

            double score = 0.0;
            boolean aiGraded = false;

            switch (question.getType()){
                case MCQ:
                case TF:
                    if (answerDto.getSelectedOptionId() != null){
                        Option selected = optionRepository.findById(answerDto.getSelectedOptionId())
                                .orElseThrow(()-> new RuntimeException("Selected option not found"));
                        if (selected.isCorrect()){
                            score=1.0;
                        }
                    }
                    break;
                case FILL_BLANK:
                    if (answerDto.getAnswerText() != null &&
                    question.getOptions().stream().anyMatch(option ->
                            option.isCorrect() &&
                            option.getText().equalsIgnoreCase(answerDto.getAnswerText().trim()))){
                        score = 1.0;
                }
                break;
                case DESCRIPTIVE:
                    score=0.0;
                    aiGraded=false;
                    break;
                default:
                    throw new RuntimeException("Unsupported question type: " + question.getType());

            }
            answer.setScore(score);
            answer.setAiGraded(aiGraded);
            answers.add(answer);
            scoreTotal += score;


        }
        submission.setScoreTotal(scoreTotal);
        submission.setAnswers(answers);

        Submission savedSubmission = submissionRepository.save(submission);
        return mapToDto(savedSubmission);
    }


    @Override
    public SubmissionDto getSubmissionById (Long id) {
        return submissionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Submission Not Found"));
    }

    @Override
    public List<SubmissionDto> getSubmissionsBYUserId(Long userId){
        return submissionRepository.findByUserId(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }


    @Override
    public List<SubmissionDto> getSubmissionsByQuizId(Long quizId){
        return submissionRepository.findByQuizId(quizId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }




    private SubmissionDto mapToDto(Submission submission){
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(submission.getId());
        submissionDto.setUserId(submission.getUserId());
        submissionDto.setQuizId(submission.getQuiz().getId());
        submissionDto.setSubmittedAt(submission.getSubmittedAt());
        submissionDto.setScoreTotal(submission.getScoreTotal());

        List<AnswerDto> answerDtos = submission.getAnswers().stream().map(answer -> {
            AnswerDto answerDto = new AnswerDto();
            answerDto.setId(answer.getId());
            answerDto.setQuestionId(answer.getQuestion().getId());
            answerDto.setAnswerText(answer.getAnswerText());
            answerDto.setSelectedOptionId(answer.getSelectedOptionId().getId());
            answerDto.setScore(answer.getScore());
            return answerDto;
        }).collect(Collectors.toList());

        submissionDto.setAnswers(answerDtos);
        return submissionDto;

    }


}
