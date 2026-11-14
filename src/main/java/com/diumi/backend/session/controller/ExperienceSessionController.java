package com.diumi.backend.session.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.session.dto.StartSessionRequest;
import com.diumi.backend.session.dto.StartSessionResponse;
import com.diumi.backend.session.service.ExperienceSessionService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class ExperienceSessionController {
    private final ExperienceSessionService sessionService;

    public ExperienceSessionController(ExperienceSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StartSessionResponse>> start(
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody StartSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sessionService.start(visitor, request)));
    }
}
