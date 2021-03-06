package com.feed_grabber.core.response;

import com.feed_grabber.core.response.dto.UserResponseShortDto;
import com.feed_grabber.core.response.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {
    Response findByRequestIdAndUserId(UUID requestId, UUID userId);

    @Query("select new com.feed_grabber.core.response.dto.UserResponseShortDto" +
            "(res.id, res.user.username, up.firstName, " +
            "up.lastName, res.answeredAt) from Request r " +
            "join r.responses res left join " +
            "res.user.userProfile up where res.answeredAt is not null and r.id = :id")
    List<UserResponseShortDto> findRespondentsByRequestId(UUID id);

    List<Response> findAllByUserIdAndRequestNotNull(UUID userId);

    @Query("select count(r) from Response r where r.request.id = :requestId and r.payload is null")
    int countUnanswered(UUID requestId);

    List<Response> findAllByUserId(UUID userId);

    @Query("select r from Response r " +
            "where r.answeredAt is null and r.request.closeDate is null and r.request.expirationDate is not null")
    List<Response> findAllToAnswer();
}
