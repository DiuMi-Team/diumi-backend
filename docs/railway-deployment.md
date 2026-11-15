# Railway 배포 가이드

## 구성

- 프론트엔드: `https://diumi.vercel.app`
- 백엔드: `https://diumi-api.up.railway.app`
- 데이터베이스: Railway MySQL
- AI: OpenAI Realtime API

## Railway 서비스 생성

1. Railway 프로젝트에서 MySQL 서비스를 추가한다.
2. GitHub의 `diumi-backend` 저장소를 연결해 백엔드 서비스를 추가한다.
3. 백엔드 서비스의 배포 브랜치를 `main`으로 설정한다.
4. Networking에서 공개 도메인을 생성하고 가능한 경우 `diumi-api`로 변경한다.
5. Healthcheck Path를 `/api/v1/actuator/health`로 설정한다.

Railway는 저장소 루트의 `Dockerfile`을 자동으로 감지해 Java 21 애플리케이션을 빌드한다.

## 백엔드 환경변수

MySQL 서비스 이름이 `MySQL`인 경우 백엔드 서비스의 Variables에 다음 값을 등록한다.

```text
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useUnicode=true
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
CORS_ALLOWED_ORIGINS=https://diumi.vercel.app
OPENAI_API_KEY=실제 OpenAI API 키
```

MySQL 서비스 이름을 다르게 정했다면 `${{MySQL.*}}`의 `MySQL` 부분을 실제 서비스 이름으로 바꾼다.
`OPENAI_API_KEY`는 GitHub나 문서에 기록하지 말고 Railway Variables에서만 입력한 뒤 Seal 처리한다.

선택 환경변수는 다음과 같다.

```text
OPENAI_BASE_URL=https://api.openai.com
OPENAI_REALTIME_MODEL=gpt-realtime
OPENAI_REALTIME_TRANSCRIPTION_MODEL=gpt-transcribe
OPENAI_REALTIME_CLIENT_SECRET_TTL_SECONDS=600
```

`PORT`는 Railway가 자동으로 제공하므로 직접 등록하지 않는다.

## 배포 확인

```text
Health:  https://diumi-api.up.railway.app/api/v1/actuator/health
Swagger: https://diumi-api.up.railway.app/api/v1/swagger-ui.html
OpenAPI: https://diumi-api.up.railway.app/api/v1/v3/api-docs
```

Health 응답이 `{"status":"UP"}`이면 서버와 데이터베이스 초기화가 완료된 상태다.
최초 실행 시 Flyway가 운영 MySQL에 필요한 테이블과 시드 데이터를 자동 반영한다.

## 프론트엔드 설정

Vercel에서 프론트엔드가 사용하는 API 주소 환경변수를 다음 값으로 설정한다.

```text
https://diumi-api.up.railway.app/api/v1
```

실제 환경변수 이름은 프론트엔드 프로젝트의 규칙(`VITE_API_BASE_URL` 등)에 맞춘다.
