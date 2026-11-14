package com.diumi.backend.action.repository;

import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.entity.ActionType;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    Optional<UserAction> findFirstBySessionIdOrderByOccurredAtDescIdDesc(UUID sessionId);

    @Query("""
            select count(action) from UserAction action
            where action.session.id = :sessionId
              and action.step.id = :stepId
              and action.valid = false
            """)
    long countInvalidActions(@Param("sessionId") UUID sessionId, @Param("stepId") Long stepId);

    @Query("""
            select count(action) from UserAction action
            where action.session.id = :sessionId
              and action.step.id = :stepId
              and action.actionType = :actionType
            """)
    long countActionsByType(@Param("sessionId") UUID sessionId, @Param("stepId") Long stepId,
                            @Param("actionType") ActionType actionType);
}
