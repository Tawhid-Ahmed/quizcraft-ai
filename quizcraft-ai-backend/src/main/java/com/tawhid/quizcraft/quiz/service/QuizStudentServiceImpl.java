package com.tawhid.quizcraft.quiz.service;

import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.auth.repository.UserRepository;
import com.tawhid.quizcraft.quiz.dto.*;
import com.tawhid.quizcraft.quiz.entity.*;
import com.tawhid.quizcraft.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizStudentServiceImpl implements QuizStudentService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;
    private final SubmissionRepository submissionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public List<QuizDto> getAvailableQuizzes() {
        return quizRepository.findByPublishedTrueAndDeletedAtIsNull().stream().map(quiz ->{
            QuizDto quizDto = new QuizDto();
            quizDto.setId(quiz.getId());
            quizDto.setTitle(quiz.getTitle());
            quizDto.setDescription(quiz.getDescription());
            quizDto.setPublished(quiz.isPublished());
            quizDto.setLanguage(quiz.getLanguage());
            quizDto.setCreatedAt(quiz.getCreatedAt());
            quizDto.setUpdatedAt(quiz.getUpdatedAt());
            return quizDto;
        } ).collect(Collectors.toList());
    }

    @Override
    public QuizDto getQuizDetails (Long quizId) {
        Quiz quiz =quizRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(quizId)
                .orElseThrow(()->new RuntimeException("Quiz Not Found"));
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quiz.getId());
        quizDto.setTitle(quiz.getTitle());
        quizDto.setDescription(quiz.getDescription());
        quizDto.setLanguage(quiz.getLanguage());

        List<QuestionDto> questionsDto = quiz.getQuestions().stream().map(question -> {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setId(question.getId());
            questionDto.setContent(question.getContent());
            questionDto.setType(question.getType());
            questionDto.setExplanation(null);
            questionDto.setMarks(question.getMarks());
            questionDto.setOptions(question.getOptions().stream().map(option -> {
                OptionDto optionDto = new OptionDto();
                optionDto.setId(option.getId());
                optionDto.setText(option.getText());
                optionDto.setCorrect(false);
                return optionDto;
            }).collect(Collectors.toList()));
            return questionDto;
        }).collect(Collectors.toList());

        quizDto.setQuestions(questionsDto);
        return quizDto;
    }

    @Override
    @Transactional
    public SubmissionDto submitQuiz(Long quizId, SubmissionDto submissionDto) {
        Quiz quiz = quizRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(quizId)
                .orElseThrow(()->new RuntimeException("Quiz Not Found"));

        User user = getCurrentUser();
        Submission submission = new Submission();
        submission.setQuiz(quiz);
        submission.setUserId(user);
        submission.setSubmittedAt(ZonedDateTime.now());
        submission = submissionRepository.save(submission);

        double scoreTotal = 0.0;
        List<Answer>  savedAnswers = new ArrayList<>();
        for (AnswerDto answerDto : submissionDto.getAnswers()) {
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question Not Found"));

            Answer answer = new Answer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
            answer.setAnswerText(answerDto.getAnswerText());
            if (answerDto.getSelectedOptionId() != null) {
                Option selectedOption = optionRepository.findById(answerDto.getSelectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Selected option not found"));
                answer.setSelectedOptionId(selectedOption);
            }
            answer.setAiGraded(false);
            double score =0.0;
            switch (question.getType()) {
                case MCQ :
                case TF:
                case FILL_BLANK:
                    if (question.getOptions().stream()
                            .anyMatch(option ->  option.getId().equals(answerDto.getSelectedOptionId()) && option.isCorrect())){
                        score =1.0;
                        answer.setAiGraded(true);
                    }
                    break;
                case DESCRIPTIVE:
                    score = 0.0;
                    answer.setAiGraded(false);
                    break;
            }
            answer.setScore(score);
            scoreTotal += score;
            savedAnswers.add(answer);

        }
        answerRepository.saveAll(savedAnswers);
        submission.setScoreTotal(scoreTotal);
        submissionRepository.save(submission);
        return toDto(submission,savedAnswers);

    }

    @Override
    public List<SubmissionDto> getMySubmissions(){
        User user = getCurrentUser();
        return submissionRepository.findByUserId(user.getId()).stream()
                .map(submission ->
                    toDto(submission,answerRepository.findBySubmissionId(submission.getId())))
                .collect(Collectors.toList());
    }


    @Override
    public SubmissionDto getSubmissionById(Long submissionId){
        User user = getCurrentUser();
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission Not Found"));

        if (!submission.getUserId().equals(user.getId())){
            throw  new SecurityException("Access Denied");

        }
        List<Answer> answers = answerRepository.findBySubmissionId(submissionId);
        return  toDto(submission,answers);
    }

    private SubmissionDto toDto(Submission submission, List<Answer> answers) {
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(submission.getId());
        submissionDto.setQuizId(submission.getQuiz().getId());
        submissionDto.setUserId(submission.getUserId());
        submissionDto.setSubmittedAt(submission.getSubmittedAt());
        submissionDto.setScoreTotal(submission.getScoreTotal());
        submissionDto.setAnswers(answers.stream().map(answer -> {
            AnswerDto answerDto = new AnswerDto();
            answerDto.setId(answer.getId());
            answerDto.setQuestionId(answer.getQuestion().getId());
            answerDto.setSelectedOptionId(answerDto.getSelectedOptionId());
            answerDto.setAnswerText(answer.getAnswerText());
            answerDto.setScore(answer.getScore());
            answerDto.setAiGraded(answerDto.isAiGraded());
            return answerDto;

        }).collect(Collectors.toList()));
        return submissionDto;
    }


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not Found"));
    }

}
