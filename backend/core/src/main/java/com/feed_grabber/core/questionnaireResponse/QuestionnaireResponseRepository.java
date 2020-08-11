package com.feed_grabber.core.questionnaireResponse;

import com.feed_grabber.core.questionnaireResponse.model.QuestionnaireResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, UUID> {
    List<QuestionnaireResponse> findAllByRequestId(UUID requestId);

    List<QuestionnaireResponse> findAllByRespondentId(UUID respondentId);

    Optional<QuestionnaireResponse> findByRequestAndRespondentId(UUID request, UUID user);
}
