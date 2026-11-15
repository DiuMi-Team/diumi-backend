package com.diumi.backend.visitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "방문자 등록 요청. 최초 방문은 빈 객체를 보내고, 재방문은 저장해 둔 visitor_id를 보냅니다.")
public record RegisterVisitorRequest(
        @Schema(description = "기존 방문자 UUID. 최초 등록이면 생략", example = "22222222-2222-4222-8222-222222222222")
        UUID visitorId
) {
}
