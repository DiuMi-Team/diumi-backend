package com.diumi.backend.session.repository;

import com.diumi.backend.session.entity.ExperienceSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceSessionRepository extends JpaRepository<ExperienceSession, UUID> {
}
