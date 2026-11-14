package com.diumi.backend.ai.controller;

import com.diumi.backend.ai.dto.StartAiConnectionRequest;
import com.diumi.backend.ai.dto.StartAiConnectionResponse;
import com.diumi.backend.ai.service.AiConnectionService;
import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions/{sessionId}/ai/connections")
public class AiConnectionController {
    private final AiConnectionService connectionService;

    public AiConnectionController(AiConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StartAiConnectionResponse>> start(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody StartAiConnectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(connectionService.start(sessionId, visitor, request)));
    }
}
