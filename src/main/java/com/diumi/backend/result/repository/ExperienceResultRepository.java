package com.diumi.backend.result.repository;

import com.diumi.backend.result.entity.ExperienceResult;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceResultRepository extends JpaRepository<ExperienceResult, UUID> {
}
