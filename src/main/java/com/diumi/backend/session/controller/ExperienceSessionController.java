package com.diumi.backend.session.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.session.dto.StartSessionRequest;
import com.diumi.backend.session.dto.StartSessionResponse;
import com.diumi.backend.session.dto.SessionDetailResponse;
import com.diumi.backend.session.service.ExperienceSessionService;
import com.diumi.backend.session.service.SessionQueryService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class ExperienceSessionController {
    private final ExperienceSessionService sessionService;
    private final SessionQueryService sessionQueryService;

    public ExperienceSessionController(ExperienceSessionService sessionService,
                                       SessionQueryService sessionQueryService) {
        this.sessionService = sessionService;
        this.sessionQueryService = sessionQueryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StartSessionResponse>> start(
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody StartSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sessionService.start(visitor, request)));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionDetailResponse>> get(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(sessionQueryService.get(sessionId, visitor)));
    }
}
