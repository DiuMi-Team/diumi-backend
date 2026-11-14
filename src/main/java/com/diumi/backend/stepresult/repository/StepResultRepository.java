package com.diumi.backend.stepresult.repository;

import com.diumi.backend.stepresult.entity.StepResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StepResultRepository extends JpaRepository<StepResult, Long> {
    boolean existsBySessionIdAndStepId(UUID sessionId, Long stepId);

    @Query("""
            select result.step.id from StepResult result
            where result.session.id = :sessionId
            order by result.step.stepOrder asc
            """)
    List<Long> findCompletedStepIds(@Param("sessionId") UUID sessionId);
}
