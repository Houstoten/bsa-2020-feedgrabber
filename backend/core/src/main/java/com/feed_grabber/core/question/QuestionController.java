
package com.feed_grabber.core.question;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feed_grabber.core.auth.security.TokenService;
import com.feed_grabber.core.question.dto.QuestionCreateDto;
import com.feed_grabber.core.question.dto.QuestionDto;
import com.feed_grabber.core.question.dto.QuestionUpdateDto;
import com.feed_grabber.core.question.exceptions.QuestionNotFoundException;
import com.feed_grabber.core.questionnaire.exceptions.QuestionnaireNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.feed_grabber.core.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @ApiOperation(value = "Get all questions from repo")
    @GetMapping()
    public AppResponse<List<QuestionDto>> getAll() {
        return new AppResponse<>(questionService.getAll(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get questions from the specific questionnaire by questionnaireID")
    @GetMapping("/questionnaires/{id}")
    public AppResponse<List<QuestionDto>> getAllByQuestionnaire(@ApiParam(
            value = "ID to get the questions list questionnaire", required = true) @PathVariable UUID id) {
        return new AppResponse<>(questionService.getAllByQuestionnaireId(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the question by id")
    @GetMapping("/{id}")
    public AppResponse<QuestionDto> getOne(@ApiParam(value = "ID to get the questionnaire",
            required = true) @PathVariable UUID id) throws QuestionnaireNotFoundException {
        return new AppResponse<>(
                questionService.getOne(id).orElseThrow(QuestionnaireNotFoundException::new),
                HttpStatus.OK
        );
    }

    @ApiOperation(value = "Create new question",
            notes = "Provide an question object with text, categoryID and questionnaireID to create new question")
    @PostMapping
    public AppResponse<QuestionDto> create(@RequestBody String json)
            throws QuestionnaireNotFoundException, JsonProcessingException {
        var dto = new ObjectMapper().readValue(json, QuestionCreateDto.class);
        var companyId = TokenService.getCompanyId();
        return new AppResponse<>(questionService.create(dto, companyId), HttpStatus.OK);
    }

    @ApiOperation(value = "Update the question",
            notes = "Provide an object with id, text, categoryID and questionnaireID to update the question")
    @PutMapping
    public AppResponse<QuestionDto> update(@RequestBody QuestionUpdateDto updateDto) throws QuestionNotFoundException {
        var companyId = TokenService.getCompanyId();
        return new AppResponse<>(questionService.update(updateDto, companyId), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete the question")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        questionService.delete(id);
    }
}
