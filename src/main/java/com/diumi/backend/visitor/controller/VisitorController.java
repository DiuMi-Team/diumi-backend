package com.diumi.backend.visitor.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.visitor.dto.RegisterVisitorRequest;
import com.diumi.backend.visitor.dto.RegisterVisitorResponse;
import com.diumi.backend.visitor.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visitors")
public class VisitorController {
    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RegisterVisitorResponse>> register(
            @RequestBody(required = false) RegisterVisitorRequest request) {
        RegisterVisitorResponse response = visitorService.register(request);
        HttpStatus status = response.isNew() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(ApiResponse.success(response));
    }
}
