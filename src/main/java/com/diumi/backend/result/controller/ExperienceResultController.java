package com.diumi.backend.result.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.result.dto.EndSessionRequest;
import com.diumi.backend.result.dto.EndSessionResponse;
import com.diumi.backend.result.dto.SessionResultResponse;
import com.diumi.backend.result.service.ExperienceResultService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions/{sessionId}")
public class ExperienceResultController {
    private final ExperienceResultService resultService;

    public ExperienceResultController(ExperienceResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<EndSessionResponse>> end(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody EndSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(resultService.end(sessionId, visitor, request)));
    }

    @GetMapping("/result")
    public ResponseEntity<ApiResponse<SessionResultResponse>> result(
            @PathVariable UUID sessionId,
            @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(resultService.getResult(sessionId, visitor)));
    }
}
