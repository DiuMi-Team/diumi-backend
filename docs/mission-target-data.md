# Mission `target_data` 계약

`Mission.target_data`는 단계 코드(`ScenarioStep.step_code`)를 키로 갖는 JSON 객체다.
각 단계에는 정답 판정에 필요한 필드만 작성하며, 작성된 필드만 요청값과 비교한다.

```json
{
  "SELECT_MENU": {
    "action_type": "SELECT",
    "target_code": "MENU_AMERICANO"
  },
  "SELECT_OPTION": {
    "action_type": "OPTION",
    "target_code": "OPTION_ICE"
  },
  "SELECT_QUANTITY": {
    "action_type": "QUANTITY",
    "action_data": {
      "quantity": 1
    }
  }
}
```

## 판정 규칙

- `action_type`: 값이 있으면 요청의 행동 유형과 일치해야 한다.
- `target_code`: 값이 있으면 요청의 대상 코드와 일치해야 한다.
- `action_data`: 값이 있으면 정의된 하위 필드가 요청 데이터와 일치해야 한다. 요청에 추가 필드가 있어도 무시한다.
- 현재 단계 코드에 해당하는 객체가 없거나 판정 필드가 하나도 없으면 `INVALID_REQUEST`를 반환한다.
- `REAL_FREE` 세션은 `target_data`를 사용하지 않고 `is_valid = null`로 저장한다.
