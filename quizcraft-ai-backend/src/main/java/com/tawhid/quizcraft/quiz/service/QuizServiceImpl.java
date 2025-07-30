package com.tawhid.quizcraft.quiz.service;


import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.common.security.SecurityUtil;
import com.tawhid.quizcraft.quiz.dto.OptionDto;
import com.tawhid.quizcraft.quiz.dto.QuestionDto;
import com.tawhid.quizcraft.quiz.dto.QuizDto;
import com.tawhid.quizcraft.quiz.entity.Option;
import com.tawhid.quizcraft.quiz.entity.Question;
import com.tawhid.quizcraft.quiz.entity.Quiz;
import com.tawhid.quizcraft.quiz.repository.OptionRepository;
import com.tawhid.quizcraft.quiz.repository.QuestionRepository;
import com.tawhid.quizcraft.quiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    @Override
    public QuizDto createQuiz(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setPublished(quizDto.isPublished());
        quiz.setLanguage(quizDto.getLanguage());
        quiz.setCreator(quizDto.getCreator());
        quiz.setCreatedAt(ZonedDateTime.now());
        quiz.setUpdatedAt(ZonedDateTime.now());

        if (quizDto.getQuestions() != null) {
            List <Question> questions = quizDto.getQuestions().stream().map(q->{
                Question question = new Question();
                question.setContent(q.getContent());
                question.setType(q.getType());
                question.setExplanation(q.getExplanation());
                question.setMarks(q.getMarks());
                question.setQuiz(quiz);

                if (q.getOptions() != null) {
                    List<Option> options = q.getOptions().stream().map(o ->{
                        Option option = new Option();
                        option.setText(o.getText());
                        option.setCorrect(o.isCorrect());
                        option.setQuestion(question);
                        return option;
                    }).collect(Collectors.toList());
                    question.setOptions(options);
                }
                return question;
            }).collect(Collectors.toList());
            quiz.setQuestions(questions);
        }
        Quiz savedQuiz = quizRepository.save(quiz);
        return mapToDto(savedQuiz);
    }

    @Override
    public QuizDto updateQuiz(Long id, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setPublished(quizDto.isPublished());
        quiz.setLanguage(quizDto.getLanguage());
        quiz.setUpdatedAt(ZonedDateTime.now());

        return mapToDto(quizRepository.save(quiz));
    }

    @Override
    public List<QuizDto> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<QuizDto> getPublishedQuizzes() {
        return quizRepository.findByPublishedTrueAndDeletedAtIsNull().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public QuizDto getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
        quiz.setDeletedAt(ZonedDateTime.now());
        quizRepository.save(quiz);
    }

    @Override
    public QuizDto togglePublishStatus (Long id, boolean published) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(published);
        quiz.setUpdatedAt(ZonedDateTime.now());
        return mapToDto(quizRepository.save(quiz));
    }
    @Override
    public List<QuizDto> getQuizzesByCurrentTeacher(){
        User current =  SecurityUtil.getCurrentUser();
        return quizRepository.findByCreatorIdAndDeletedAtIsNull(current.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private QuizDto mapToDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quiz.getId());
        quizDto.setTitle(quiz.getTitle());
        quizDto.setDescription(quiz.getDescription());
        quizDto.setCreator(quiz.getCreator());
        quizDto.setPublished(quiz.isPublished());
        quizDto.setLanguage(quiz.getLanguage());
        quizDto.setCreatedAt(quiz.getCreatedAt());
        quizDto.setUpdatedAt(quiz.getUpdatedAt());
        if (quiz.getQuestions() != null) {
            List<QuestionDto> questionDtos = quiz.getQuestions().stream().map(q->{
                QuestionDto questionDto = new QuestionDto();
                questionDto.setId(q.getId());
                questionDto.setContent(q.getContent());
                questionDto.setType(q.getType());
                questionDto.setExplanation(q.getExplanation());
                questionDto.setMarks(q.getMarks());

                if (q.getOptions() != null) {
                    List<OptionDto> optionDtos = q.getOptions().stream().map(o->{
                        OptionDto optionDto = new OptionDto();
                        optionDto.setId(o.getId());
                        optionDto.setText(o.getText());
                        optionDto.setCorrect(o.isCorrect());
                        return optionDto;
                    }).collect(Collectors.toList());
                    questionDto.setOptions(optionDtos);
                }
                return questionDto;
            }).collect(Collectors.toList());
            quizDto.setQuestions(questionDtos);
        }
        return quizDto;
    }

}
