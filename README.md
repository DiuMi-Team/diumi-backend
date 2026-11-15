# diumi-backend
디우미 - AI 기반 시니어 디지털 생활 도우미 서비스 백엔드

## API 문서

애플리케이션 실행 후 다음 주소에서 API 명세를 확인하고 직접 요청할 수 있습니다.

- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v1/v3/api-docs`

방문자 등록(`POST /visitors`)을 제외한 API는 Swagger UI의 `X-Visitor-Id` 헤더에
방문자 등록 API가 반환한 UUID를 입력해야 합니다.
