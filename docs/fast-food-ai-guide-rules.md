# 패스트푸드 AI 음성 도우미 규칙

AI 음성 도우미는 화면을 직접 조작하지 않는다. 사용자가 "불고기버거 세트로 해 줘"라고
말해도 선택이 완료된 것으로 처리하지 않고, 사용자가 직접 눌러야 할 버튼만 안내한다.

API 8을 호출할 때마다 백엔드는 현재 미션, 진행 단계와 세부 화면 정보를 새 Realtime 세션의
프롬프트에 다시 넣는다. 이전 대화의 기억에 의존하지 않는다.

```json
{
  "step_id": 9001,
  "screen_code": "SELECT_MEAL_TYPE"
}
```

- `step_id`: 단계 완료와 결과 계산을 위한 큰 진행 단계
- `screen_code`: 사용자가 현재 보고 있는 세부 화면
- `screen_code` 생략 시 `ScenarioStep.step_code` 사용
- 등록되지 않은 코드는 `400 INVALID_REQUEST`

## 패스트푸드 화면 코드

| 화면 | screen_code |
|---|---|
| 오늘의 미션 | `MISSION_BRIEFING` |
| 매장/포장 | `SELECT_ORDER_TYPE` |
| 메뉴 카테고리 | `SELECT_CATEGORY` |
| 메뉴 선택 | `SELECT_MENU` |
| 단품/세트 | `SELECT_MEAL_TYPE` |
| 세트 크기 | `SELECT_SET_SIZE` |
| 사이드 선택 | `SELECT_SIDE` |
| 음료 선택 | `SELECT_DRINK` |
| 재료/추가 옵션 | `SELECT_OPTION` |
| 수량 | `SELECT_QUANTITY` |
| 추가 메뉴 | `SELECT_EXTRA_MENU` |
| 장바구니 | `CART` |
| 주문 확인 | `ORDER_REVIEW` |
| 쿠폰/적립 | `MEMBERSHIP` |
| 결제 방법 | `SELECT_PAYMENT` |
| 카드 결제 | `PAY_CARD` |
| 모바일 결제 | `PAY_MOBILE` |
| 바코드/QR 결제 | `PAY_BARCODE` |
| 주문 완료 | `ORDER_COMPLETE` |

규칙은 `ai_guide_rule` 테이블에서 `scenario_type = 'FAST_FOOD'`로 관리한다. 프론트엔드는
AI 음성 연결 API를 호출할 때 현재 화면과 일치하는 `screen_code`를 전달해야 한다.
