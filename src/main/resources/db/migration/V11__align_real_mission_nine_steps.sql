-- 실전 진행바(9단계)에 맞춰 카페·패스트푸드 단계를 바꾸고, 미션 정답과 완료 조건을 디자인 시안에 맞춘다.
--
-- 1) 기존 단계 행(step_id 11~14, 21~26)은 이미 쌓인 기록(user_action, step_result)이 가리키고 있어 지우지 않고 내용을 바꾼다.
--    순서·코드 중복을 피하려고 먼저 임시 값으로 바꾼 뒤 새 값을 넣는다.
-- 2) target_data 형식
--    - 단계 코드: {"accept": [맞는 행동 목록]} 목록 중 하나와 같으면 정답이다.
--    - conditions: 결과 화면의 완료 조건. ACTION은 그 단계의 마지막 행동 코드를, CART_ITEM은 장바구니(action_data.items)를 본다.

DELETE FROM mission_step_guide WHERE mission_id IN (101, 102, 201, 202);

UPDATE scenario_step
SET step_order = step_order + 100,
    step_code = CONCAT('OLD_', step_id)
WHERE scenario_id IN (1, 2);

UPDATE scenario_step SET step_code = 'SELECT_ORDER_TYPE', step_order = 1, step_name = '매장·포장 고르기', guide_message = '매장에서 드실지, 포장해 가실지 골라 주세요.', fallback_message = '화면의 매장 또는 포장 버튼을 눌러 보세요.' WHERE step_id = 11;
UPDATE scenario_step SET step_code = 'SELECT_MENU', step_order = 2, step_name = '메뉴 고르기', guide_message = '미션에 적힌 음료를 눌러 주세요.', fallback_message = '음료 사진이나 이름을 눌러 보세요.' WHERE step_id = 12;
UPDATE scenario_step SET step_code = 'SELECT_OPTION', step_order = 3, step_name = '옵션 고르기', guide_message = '온도, 크기, 수량을 고른 뒤 담기를 눌러 주세요.', fallback_message = 'HOT은 따뜻하게, ICE는 차갑게예요. 수량은 + 버튼으로 늘려요.' WHERE step_id = 13;
UPDATE scenario_step SET step_code = 'ADD_MORE_MENU', step_order = 4, step_name = '메뉴 더 담기', guide_message = '더 담을 메뉴가 없으면 주문하러 가기를 눌러 주세요.', fallback_message = '다른 메뉴가 필요하면 메뉴 더 담기를 눌러요.' WHERE step_id = 14;

INSERT INTO scenario_step (step_id, scenario_id, step_code, step_order, step_name, guide_message, fallback_message) VALUES
    (15, 1, 'CART', 5, '장바구니 확인', '담은 메뉴와 수량을 확인하고 주문하기를 눌러 주세요.', '화면 아래 주문하기 버튼을 눌러 보세요.'),
    (16, 1, 'MEMBERSHIP', 6, '멤버십·쿠폰', '멤버십이나 쿠폰이 없으면 건너뛰기를 눌러 주세요.', '화면 아래 건너뛰기 버튼을 눌러 보세요.'),
    (17, 1, 'SELECT_PAYMENT', 7, '결제 방식 고르기', '미션에 적힌 결제 방법을 골라 주세요.', '카드로 결제하려면 신용·체크카드를 눌러 보세요.'),
    (18, 1, 'PAYMENT', 8, '결제하기', '안내에 따라 결제를 마쳐 주세요.', '카드를 카드 투입구에 꽂아 보세요.'),
    (19, 1, 'ORDER_COMPLETE', 9, '결제 완료', '대기번호를 확인하고 확인 버튼을 눌러 주세요.', '화면의 확인 버튼을 눌러 보세요.');

UPDATE scenario_step SET step_code = 'SELECT_ORDER_TYPE', step_order = 1, step_name = '매장·포장 고르기', guide_message = '매장에서 드실지, 포장해 가실지 골라 주세요.', fallback_message = '화면의 매장 또는 포장 버튼을 눌러 보세요.' WHERE step_id = 21;
UPDATE scenario_step SET step_code = 'SELECT_MENU', step_order = 2, step_name = '메뉴 고르기', guide_message = '미션에 적힌 버거를 골라 주세요.', fallback_message = '메뉴 이름과 같은 버거 버튼을 눌러 보세요.' WHERE step_id = 22;
UPDATE scenario_step SET step_code = 'SELECT_MEAL_TYPE', step_order = 3, step_name = '세트·단품 고르기', guide_message = '단품과 세트 중 미션에 맞는 구성을 골라 주세요.', fallback_message = '감자튀김과 음료가 필요하면 세트를 눌러 보세요.' WHERE step_id = 23;
UPDATE scenario_step SET step_code = 'SELECT_SIDE_DRINK', step_order = 4, step_name = '사이드·음료 고르기', guide_message = '세트에 넣을 사이드와 음료를 골라 주세요.', fallback_message = '미션에 적힌 사이드와 음료 버튼을 눌러 보세요.' WHERE step_id = 24;
UPDATE scenario_step SET step_code = 'ADD_TO_CART', step_order = 5, step_name = '장바구니 담기', guide_message = '고른 구성을 확인하고 장바구니 담기를 눌러 주세요.', fallback_message = '화면 아래 장바구니 담기 버튼을 눌러 보세요.' WHERE step_id = 25;
UPDATE scenario_step SET step_code = 'SELECT_EXTRA_MENU', step_order = 6, step_name = '메뉴 더 고르기', guide_message = '더 고를 메뉴가 없으면 주문하러 가기를 눌러 주세요.', fallback_message = '다른 메뉴가 필요하면 메뉴 더 고르기를 눌러요.' WHERE step_id = 26;

INSERT INTO scenario_step (step_id, scenario_id, step_code, step_order, step_name, guide_message, fallback_message) VALUES
    (27, 2, 'ORDER_REVIEW', 7, '주문 확인', '주문 내역을 확인하고 결제하기를 눌러 주세요.', '화면 아래 결제하기 버튼을 눌러 보세요.'),
    (28, 2, 'PAYMENT', 8, '결제하기', '미션에 적힌 결제 방법을 골라 결제해 주세요.', '카드로 결제하려면 신용·체크카드를 눌러 보세요.'),
    (29, 2, 'ORDER_COMPLETE', 9, '결제 완료', '대기번호를 확인하고 확인 버튼을 눌러 주세요.', '화면의 확인 버튼을 눌러 보세요.');

UPDATE mission
SET title = '아이스 아메리카노 Large 2잔 포장 주문하기',
    description = '아이스 아메리카노 Large 2잔을 포장으로 주문하세요.\n결제: 신용·체크카드',
    estimated_minutes = 3,
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_TAKE_OUT"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_AMERICANO"}]},"SELECT_OPTION":{"accept":[{"action_type":"OPTION","target_code":"TEMP_ICE"},{"action_type":"OPTION","target_code":"SIZE_LARGE"},{"action_type":"QUANTITY","target_code":"QUANTITY_PLUS","action_data":{"quantity":2}},{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"ADD_MORE_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"MEMBERSHIP":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_SKIP"}]},"SELECT_PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"}]},"PAYMENT":{"accept":[{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"MENU","label":"아이스 아메리카노 선택","source":"CART_ITEM","step_code":"CART","action_type":"NEXT","menu":"MENU_AMERICANO","expected":{"temperature":"TEMP_ICE"},"quantity_unit":"잔"},{"code":"SIZE_QUANTITY","label":"Large 사이즈 · 수량 2잔","source":"CART_ITEM","step_code":"CART","action_type":"NEXT","menu":"MENU_AMERICANO","expected":{"size":"SIZE_LARGE","quantity":2},"quantity_unit":"잔"},{"code":"ORDER_TYPE","label":"포장 선택","source":"ACTION","step_code":"SELECT_ORDER_TYPE","action_type":"SELECT","expected_target_code":"ORDER_TAKE_OUT"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 101;

UPDATE mission
SET title = '따뜻한 카페라테 주문하기',
    description = '① 따뜻한 카페라테 1잔 담기\n② 카드로 결제하기',
    estimated_minutes = 4,
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_EAT_IN"},{"action_type":"SELECT","target_code":"ORDER_TAKE_OUT"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_CAFE_LATTE"}]},"SELECT_OPTION":{"accept":[{"action_type":"OPTION","target_code":"TEMP_HOT"},{"action_type":"OPTION","target_code":"SIZE_REGULAR"},{"action_type":"OPTION","target_code":"SIZE_LARGE"},{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"ADD_MORE_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"MEMBERSHIP":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_SKIP"}]},"SELECT_PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"}]},"PAYMENT":{"accept":[{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"MENU","label":"따뜻한 카페라테 1잔","source":"CART_ITEM","step_code":"CART","action_type":"NEXT","menu":"MENU_CAFE_LATTE","expected":{"temperature":"TEMP_HOT","quantity":1},"quantity_unit":"잔"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 102;

UPDATE mission
SET title = '불고기버거 세트 매장 주문하기',
    description = '불고기버거 세트 1개를 매장에서 먹도록 주문하세요.\n사이드: 감자튀김 · 음료: 콜라\n결제: 신용·체크카드',
    estimated_minutes = 5,
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_EAT_IN"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_BULGOGI_BURGER"}]},"SELECT_MEAL_TYPE":{"accept":[{"action_type":"OPTION","target_code":"MEAL_SET"}]},"SELECT_SIDE_DRINK":{"accept":[{"action_type":"OPTION","target_code":"SIDE_FRIES"},{"action_type":"OPTION","target_code":"DRINK_COLA"},{"action_type":"NEXT","target_code":"BUTTON_NEXT"}]},"ADD_TO_CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"SELECT_EXTRA_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"ORDER_REVIEW":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"},{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"ORDER_TYPE","label":"매장 식사 선택","source":"ACTION","step_code":"SELECT_ORDER_TYPE","action_type":"SELECT","expected_target_code":"ORDER_EAT_IN"},{"code":"MENU","label":"불고기버거 세트 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_BULGOGI_BURGER","expected":{"meal_type":"MEAL_SET","quantity":1},"quantity_unit":"개"},{"code":"SIDE_DRINK","label":"감자튀김 · 콜라 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_BULGOGI_BURGER","expected":{"side":"SIDE_FRIES","drink":"DRINK_COLA"},"quantity_unit":"개"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 201;

UPDATE mission
SET title = '치킨버거 라지 세트 주문하기',
    description = '① 치킨버거 라지 세트 선택\n② 감자튀김과 제로 콜라 선택\n③ 카드로 결제하기',
    estimated_minutes = 7,
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_EAT_IN"},{"action_type":"SELECT","target_code":"ORDER_TAKE_OUT"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_CHICKEN_BURGER"}]},"SELECT_MEAL_TYPE":{"accept":[{"action_type":"OPTION","target_code":"MEAL_SET"},{"action_type":"OPTION","target_code":"MEAL_LARGE_SET"}]},"SELECT_SIDE_DRINK":{"accept":[{"action_type":"OPTION","target_code":"SIDE_FRIES"},{"action_type":"OPTION","target_code":"DRINK_ZERO_COLA"},{"action_type":"NEXT","target_code":"BUTTON_NEXT"}]},"ADD_TO_CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"SELECT_EXTRA_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"ORDER_REVIEW":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"},{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"MENU","label":"치킨버거 라지 세트 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_CHICKEN_BURGER","expected":{"meal_type":"MEAL_LARGE_SET","quantity":1},"quantity_unit":"개"},{"code":"SIDE_DRINK","label":"감자튀김 · 제로콜라 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_CHICKEN_BURGER","expected":{"side":"SIDE_FRIES","drink":"DRINK_ZERO_COLA"},"quantity_unit":"개"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 202;

INSERT INTO mission_step_guide (mission_id, step_id, guide_message, highlight_target_code) VALUES
    (101, 11, '포장해 가실 거니까 포장을 눌러 주세요.', 'ORDER_TAKE_OUT'),
    (101, 12, '아메리카노를 눌러 주세요.', 'MENU_AMERICANO'),
    (101, 13, 'ICE, Large를 고르고 + 버튼으로 2잔을 만든 뒤 담기를 눌러 주세요.', 'TEMP_ICE'),
    (101, 14, '더 담을 메뉴가 없으니 주문하러 가기를 눌러 주세요.', 'BUTTON_GO_CART'),
    (101, 15, '아이스 아메리카노 Large 2잔이 맞는지 보고 주문하기를 눌러 주세요.', 'BUTTON_ORDER'),
    (101, 16, '건너뛰기를 눌러 주세요.', 'BUTTON_SKIP'),
    (101, 17, '신용·체크카드를 눌러 주세요.', 'PAY_CARD'),
    (101, 18, '카드를 투입구에 꽂아 결제해 주세요.', 'PAY_CARD'),
    (101, 19, '확인 버튼을 눌러 주세요.', 'BUTTON_DONE'),
    (102, 11, '매장 또는 포장 중 하나를 눌러 주세요.', 'ORDER_EAT_IN'),
    (102, 12, '카페라테를 눌러 주세요.', 'MENU_CAFE_LATTE'),
    (102, 13, '따뜻한 음료니까 HOT을 고르고 담기를 눌러 주세요.', 'TEMP_HOT'),
    (102, 14, '더 담을 메뉴가 없으니 주문하러 가기를 눌러 주세요.', 'BUTTON_GO_CART'),
    (102, 15, '따뜻한 카페라테 1잔이 맞는지 보고 주문하기를 눌러 주세요.', 'BUTTON_ORDER'),
    (102, 16, '건너뛰기를 눌러 주세요.', 'BUTTON_SKIP'),
    (102, 17, '신용·체크카드를 눌러 주세요.', 'PAY_CARD'),
    (102, 18, '카드를 투입구에 꽂아 결제해 주세요.', 'PAY_CARD'),
    (102, 19, '확인 버튼을 눌러 주세요.', 'BUTTON_DONE'),
    (201, 21, '매장에서 드실 거니까 매장을 눌러 주세요.', 'ORDER_EAT_IN'),
    (201, 22, '불고기버거를 눌러 주세요.', 'MENU_BULGOGI_BURGER'),
    (201, 23, '세트를 눌러 주세요.', 'MEAL_SET'),
    (201, 24, '감자튀김과 콜라를 고르고 다음을 눌러 주세요.', 'SIDE_FRIES'),
    (201, 25, '장바구니 담기를 눌러 주세요.', 'BUTTON_ADD_TO_CART'),
    (201, 26, '더 고를 메뉴가 없으니 주문하러 가기를 눌러 주세요.', 'BUTTON_GO_CART'),
    (201, 27, '불고기버거 세트 1개가 맞는지 보고 결제하기를 눌러 주세요.', 'BUTTON_ORDER'),
    (201, 28, '신용·체크카드를 고르고 카드를 꽂아 주세요.', 'PAY_CARD'),
    (201, 29, '확인 버튼을 눌러 주세요.', 'BUTTON_DONE'),
    (202, 21, '매장 또는 포장 중 하나를 눌러 주세요.', 'ORDER_EAT_IN'),
    (202, 22, '치킨버거를 눌러 주세요.', 'MENU_CHICKEN_BURGER'),
    (202, 23, '라지 세트를 골라 주세요.', 'MEAL_LARGE_SET'),
    (202, 24, '감자튀김과 제로 콜라를 고르고 다음을 눌러 주세요.', 'DRINK_ZERO_COLA'),
    (202, 25, '장바구니 담기를 눌러 주세요.', 'BUTTON_ADD_TO_CART'),
    (202, 26, '더 고를 메뉴가 없으니 주문하러 가기를 눌러 주세요.', 'BUTTON_GO_CART'),
    (202, 27, '치킨버거 라지 세트가 맞는지 보고 결제하기를 눌러 주세요.', 'BUTTON_ORDER'),
    (202, 28, '신용·체크카드를 고르고 카드를 꽂아 주세요.', 'PAY_CARD'),
    (202, 29, '확인 버튼을 눌러 주세요.', 'BUTTON_DONE');
