package com.feed_grabber.core.questionnaire;

import com.feed_grabber.core.apiContract.AppResponse;
import com.feed_grabber.core.apiContract.DataList;
import com.feed_grabber.core.auth.security.TokenService;
import com.feed_grabber.core.company.exceptions.CompanyNotFoundException;
import com.feed_grabber.core.exceptions.NotFoundException;
import com.feed_grabber.core.questionnaire.dto.QuestionnaireCreateDto;
import com.feed_grabber.core.questionnaire.dto.QuestionnaireDto;
import com.feed_grabber.core.questionnaire.dto.QuestionnaireFullDto;
import com.feed_grabber.core.questionnaire.dto.QuestionnaireUpdateDto;
import com.feed_grabber.core.questionnaire.exceptions.QuestionnaireExistsException;
import com.feed_grabber.core.questionnaire.exceptions.QuestionnaireNotFoundException;
import com.feed_grabber.core.sections.SectionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.feed_grabber.core.role.RoleConstants.ROLE_COMPANY_OWNER;
import static com.feed_grabber.core.role.RoleConstants.ROLE_HR;

@RestController
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {
    private final QuestionnaireService questionnaireService;
    private final SectionService sectionService;

    @Autowired
    public QuestionnaireController(QuestionnaireService questionnaireService, SectionService sectionService) {
        this.questionnaireService = questionnaireService;
        this.sectionService = sectionService;
    }

    @ApiOperation("Get all questionnaires")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Secured(value = {ROLE_COMPANY_OWNER, ROLE_HR})
    public AppResponse<DataList<QuestionnaireDto>> getAll(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(defaultValue = "false") boolean archived
    ) {
        var companyId = TokenService.getCompanyId();
        var dataList = new DataList<>(
                questionnaireService.getAllByCompanyId(companyId, page, size, archived),
                questionnaireService.getCountByCompanyId(companyId, archived),
                page,
                size
        );
        return new AppResponse<>(dataList);
    }

    @ApiOperation("Get one questionnaire by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppResponse<QuestionnaireDto> getOne(@PathVariable UUID id) throws QuestionnaireNotFoundException {
        return new AppResponse<>(
                questionnaireService.getOne(id)
                        .orElseThrow(QuestionnaireNotFoundException::new)
        );
    }

    @GetMapping("/{id}/sections")
    @ResponseStatus(HttpStatus.OK)
    public AppResponse<QuestionnaireFullDto> getFullOne(@PathVariable UUID id) throws QuestionnaireNotFoundException {
        var q = questionnaireService.getOne(id).orElseThrow(QuestionnaireNotFoundException::new);
        return new AppResponse<>(new QuestionnaireFullDto(
                q.getId(),
                q.getTitle(),
                q.getCompanyName(),
                q.isEditingEnabled(),
                sectionService.getByQuestionnaire(id)
        ));
    }

    @ApiOperation(value = "Get requests for one questionnaire")
    @GetMapping("/requests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppResponse<QuestionnaireDto> getRequests(@PathVariable UUID id) throws QuestionnaireNotFoundException {
        return new AppResponse<>(
                questionnaireService.getOne(id)
                        .orElseThrow(QuestionnaireNotFoundException::new)
        );
    }

    @ApiOperation("Create a questionnaire")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(value = {ROLE_COMPANY_OWNER, ROLE_HR})
    public AppResponse<QuestionnaireDto> create(@RequestBody QuestionnaireCreateDto createDto) throws NotFoundException, QuestionnaireExistsException  {
        return new AppResponse<>(
                questionnaireService.create(createDto, TokenService.getCompanyId())
        );
    }

    @ApiOperation("Update the questionnaire")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Secured(value = {ROLE_COMPANY_OWNER, ROLE_HR})
    public AppResponse<QuestionnaireDto> update(@RequestBody QuestionnaireUpdateDto updateDto) throws QuestionnaireNotFoundException, CompanyNotFoundException, QuestionnaireExistsException {
        return new AppResponse<>(
                questionnaireService.update(updateDto, TokenService.getCompanyId())
        );
    }

    @ApiOperation("Delete one")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(value = {ROLE_COMPANY_OWNER, ROLE_HR})
    public void delete(@PathVariable UUID id) {
        questionnaireService.delete(id);
    }

}
