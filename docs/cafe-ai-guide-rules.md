# 카페 AI 음성 도우미 규칙

AI 음성 도우미는 화면을 조작하지 않는다. 사용자가 "카드로 할래"라고 말해도 선택이 완료된 것으로 처리하지 않고, "카드 결제 버튼을 직접 눌러 주세요"라고 안내한다.

API 8을 호출할 때마다 백엔드는 다음 정보를 새 Realtime 세션의 프롬프트에 넣는다.

- 오늘의 미션 제목, 설명, 전체 `target_data`
- 현재 `ScenarioStep.step_code`에 해당하는 목표
- 현재 단계 이름과 안내 문구
- `COMMON` 규칙과 현재 `step_code`에 해당하는 카페 안내 규칙

## 카페 단계 코드

카페 시나리오의 `ScenarioStep.step_code`는 아래 코드와 맞춰야 해당 화면 규칙이 적용된다.

| 화면 | step_code |
|---|---|
| 오늘의 미션 | `MISSION_BRIEFING` |
| 매장/포장 | `SELECT_ORDER_TYPE` |
| 메뉴 선택 | `SELECT_MENU` |
| 옵션 선택 | `SELECT_OPTION` |
| 수량/추가 옵션 | `SELECT_QUANTITY` |
| 장바구니 | `CART` |
| 주문 확인 | `ORDER_REVIEW` |
| 적립/쿠폰 | `MEMBERSHIP` |
| 결제 방법 | `SELECT_PAYMENT` |
| 카드 결제 | `PAY_CARD` |
| 모바일 결제 | `PAY_MOBILE` |
| 바코드 결제 | `PAY_BARCODE` |
| 주문 완료 | `ORDER_COMPLETE` |

규칙은 `ai_guide_rule` 테이블에 저장하며, 문구를 바꿀 때 애플리케이션 코드를 수정하지 않고 DB 데이터로 관리할 수 있다.
