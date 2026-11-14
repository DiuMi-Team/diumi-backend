package com.diumi.backend.stepresult.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.stepresult.dto.CompleteStepRequest;
import com.diumi.backend.stepresult.dto.CompleteStepResponse;
import com.diumi.backend.stepresult.service.StepResultService;
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
@RequestMapping("/sessions/{sessionId}/steps/{stepId}/complete")
public class StepResultController {
    private final StepResultService stepResultService;

    public StepResultController(StepResultService stepResultService) {
        this.stepResultService = stepResultService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompleteStepResponse>> complete(
            @PathVariable UUID sessionId,
            @PathVariable Long stepId,
            @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody CompleteStepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(stepResultService.complete(sessionId, stepId, visitor, request)));
    }
}
