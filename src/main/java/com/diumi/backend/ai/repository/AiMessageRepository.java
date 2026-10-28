package com.diumi.backend.ai.repository;

import com.diumi.backend.ai.entity.AiMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiMessageRepository extends JpaRepository<AiMessage, Long> {
}
