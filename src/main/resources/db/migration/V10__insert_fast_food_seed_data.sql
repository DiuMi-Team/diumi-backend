-- 패스트푸드 공식 시나리오를 활성화하고 큰 진행 단계와 미션을 등록한다.
-- 장바구니, 세트 크기, 결제 수단별 화면 등은 API 8의 screen_code로 구분한다.

UPDATE scenario
SET is_active = TRUE
WHERE scenario_id = 2
  AND scenario_type = 'FAST_FOOD';

INSERT INTO scenario_step
    (step_id, scenario_id, step_code, step_order, step_name, guide_message, fallback_message)
VALUES
    (21, 2, 'SELECT_MENU', 1, '메뉴 고르기',
     '오늘의 미션에 적힌 버거를 골라 주세요.', '메뉴 이름과 같은 버거 버튼을 눌러 보세요.'),
    (22, 2, 'SELECT_MEAL_TYPE', 2, '단품 또는 세트 고르기',
     '단품과 세트 중 미션에 맞는 구성을 골라 주세요.', '감자튀김과 음료가 필요하면 세트 버튼을 눌러 보세요.'),
    (23, 2, 'SELECT_SIDE', 3, '사이드 고르기',
     '세트에 포함할 사이드 메뉴를 골라 주세요.', '미션에 적힌 사이드와 같은 버튼을 눌러 보세요.'),
    (24, 2, 'SELECT_DRINK', 4, '음료 고르기',
     '세트에 포함할 음료를 골라 주세요.', '미션에 적힌 음료와 같은 버튼을 눌러 보세요.'),
    (25, 2, 'CONFIRM_ORDER', 5, '주문 확인',
     '메뉴와 세트 구성을 확인하고 주문하기를 눌러 주세요.', '화면 아래 주문하기 버튼을 눌러 보세요.'),
    (26, 2, 'PAYMENT', 6, '결제하기',
     '미션에 적힌 결제 방법을 골라 주세요.', '카드로 결제하려면 카드 결제 버튼을 눌러 보세요.');

INSERT INTO mission
    (mission_id, scenario_id, title, description, difficulty, target_data,
     is_active, estimated_minutes)
VALUES
    (201, 2, '불고기버거 세트 주문하기',
     '① 불고기버거 세트 선택\n② 감자튀김과 콜라 선택\n③ 카드로 결제하기',
     'EASY',
     '{"SELECT_MENU":{"action_type":"SELECT","target_code":"MENU_BULGOGI_BURGER"},"SELECT_MEAL_TYPE":{"action_type":"OPTION","target_code":"MEAL_SET"},"SELECT_SIDE":{"action_type":"OPTION","target_code":"SIDE_FRIES"},"SELECT_DRINK":{"action_type":"OPTION","target_code":"DRINK_COLA"},"CONFIRM_ORDER":{"action_type":"NEXT"},"PAYMENT":{"action_type":"PAY","target_code":"PAY_CARD"}}',
     TRUE, 5),
    (202, 2, '치킨버거 라지 세트 주문하기',
     '① 치킨버거 세트 선택\n② 감자튀김과 제로 콜라 선택\n③ 카드로 결제하기',
     'NORMAL',
     '{"SELECT_MENU":{"action_type":"SELECT","target_code":"MENU_CHICKEN_BURGER"},"SELECT_MEAL_TYPE":{"action_type":"OPTION","target_code":"MEAL_LARGE_SET"},"SELECT_SIDE":{"action_type":"OPTION","target_code":"SIDE_FRIES"},"SELECT_DRINK":{"action_type":"OPTION","target_code":"DRINK_ZERO_COLA"},"CONFIRM_ORDER":{"action_type":"NEXT"},"PAYMENT":{"action_type":"PAY","target_code":"PAY_CARD"}}',
     TRUE, 7);

INSERT INTO mission_step_guide
    (mission_id, step_id, guide_message, highlight_target_code)
VALUES
    (201, 21, '불고기버거를 눌러 주세요.', 'MENU_BULGOGI_BURGER'),
    (201, 22, '세트 메뉴 버튼을 눌러 주세요.', 'MEAL_SET'),
    (201, 23, '감자튀김을 눌러 주세요.', 'SIDE_FRIES'),
    (201, 24, '콜라를 눌러 주세요.', 'DRINK_COLA'),
    (201, 25, '주문 내용을 확인하고 주문하기를 눌러 주세요.', 'BUTTON_NEXT'),
    (201, 26, '카드 결제를 눌러 주세요.', 'PAY_CARD'),
    (202, 21, '치킨버거를 눌러 주세요.', 'MENU_CHICKEN_BURGER'),
    (202, 22, '라지 세트 버튼을 눌러 주세요.', 'MEAL_LARGE_SET'),
    (202, 23, '감자튀김을 눌러 주세요.', 'SIDE_FRIES'),
    (202, 24, '제로 콜라를 눌러 주세요.', 'DRINK_ZERO_COLA'),
    (202, 25, '주문 내용을 확인하고 주문하기를 눌러 주세요.', 'BUTTON_NEXT'),
    (202, 26, '카드 결제를 눌러 주세요.', 'PAY_CARD');
