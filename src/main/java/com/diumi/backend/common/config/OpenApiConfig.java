package com.diumi.backend.common.config;

import com.diumi.backend.visitor.controller.VisitorController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "디우미(Diumi) API",
                version = "v1",
                description = "시니어를 위한 키오스크 체험 및 AI 음성 안내 서비스의 백엔드 API입니다. "
                        + "모든 응답은 success, data, error 필드를 가진 공통 형식을 사용합니다.",
                license = @License(name = "Diumi Team")
        ),
        tags = {
                @Tag(name = "1. 방문자", description = "익명 방문자 등록"),
                @Tag(name = "2. 시나리오", description = "체험 시나리오 목록 및 상세 조회"),
                @Tag(name = "3. 미션", description = "시나리오별 미션 목록 조회"),
                @Tag(name = "4. 체험 세션", description = "체험 세션 시작 및 진행 상태 조회"),
                @Tag(name = "5. 행동", description = "체험 중 사용자 행동 기록"),
                @Tag(name = "6. 단계", description = "체험 단계 완료 기록"),
                @Tag(name = "7. AI", description = "AI 음성 연결 및 대화 기록 저장"),
                @Tag(name = "8. 결과", description = "체험 세션 종료 및 수행 결과 조회")
        }
)
public class OpenApiConfig {
    private static final String VISITOR_ID_EXAMPLE = "22222222-2222-4222-8222-222222222222";

    @Bean
    public OperationCustomizer visitorHeaderOperationCustomizer() {
        return (operation, handlerMethod) -> {
            if (!VisitorController.class.isAssignableFrom(handlerMethod.getBeanType())) {
                operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("X-Visitor-Id")
                        .required(true)
                        .description("방문자 등록 API에서 발급받아 브라우저에 저장한 방문자 UUID")
                        .example(VISITOR_ID_EXAMPLE)
                        .schema(new StringSchema().format("uuid")));
            }
            return operation;
        };
    }
}
