package com.diumi.backend.ai.screen.repository;

import com.diumi.backend.ai.screen.entity.AiScreenButton;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiScreenButtonRepository extends JpaRepository<AiScreenButton, Long> {
    List<AiScreenButton> findByScreenIdOrderByDisplayOrderAscIdAsc(Long screenId);
}
