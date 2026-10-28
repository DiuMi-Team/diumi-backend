package com.diumi.backend.ai.controller;

import com.diumi.backend.ai.dto.SaveAiMessagesRequest;
import com.diumi.backend.ai.dto.SaveAiMessagesResponse;
import com.diumi.backend.ai.service.AiMessageService;
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
@RequestMapping("/sessions/{sessionId}/ai/messages")
public class AiMessageController {
    private final AiMessageService messageService;

    public AiMessageController(AiMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaveAiMessagesResponse>> save(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody SaveAiMessagesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(messageService.save(sessionId, visitor, request)));
    }
}
