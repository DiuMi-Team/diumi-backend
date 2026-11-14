package com.diumi.backend.action.controller;

import com.diumi.backend.action.dto.RecordActionRequest;
import com.diumi.backend.action.dto.RecordActionResponse;
import com.diumi.backend.action.service.UserActionService;
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
@RequestMapping("/sessions/{sessionId}/actions")
public class UserActionController {
    private final UserActionService actionService;

    public UserActionController(UserActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecordActionResponse>> record(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody RecordActionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(actionService.record(sessionId, visitor, request)));
    }
}
