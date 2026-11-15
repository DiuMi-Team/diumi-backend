package com.diumi.backend.visitor.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.visitor.dto.RegisterVisitorRequest;
import com.diumi.backend.visitor.dto.RegisterVisitorResponse;
import com.diumi.backend.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visitors")
@Tag(name = "1. 방문자")
public class VisitorController {
    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    @Operation(summary = "방문자 등록", description = "visitor_id가 없으면 새 UUID를 발급하고, 있으면 기존 방문자의 마지막 접근 시간을 갱신합니다. 이 API만 X-Visitor-Id 헤더가 필요하지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "신규 방문자 등록 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기존 방문자 접근 시간 갱신 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 visitor_id 형식(INVALID_REQUEST)")
    })
    public ResponseEntity<ApiResponse<RegisterVisitorResponse>> register(
            @RequestBody(required = false) RegisterVisitorRequest request) {
        RegisterVisitorResponse response = visitorService.register(request);
        HttpStatus status = response.isNew() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(ApiResponse.success(response));
    }
}
