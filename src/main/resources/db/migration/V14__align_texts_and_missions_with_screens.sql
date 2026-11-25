-- 배포된 프론트(https://diumi.vercel.app) 실전 화면에 맞춰 문구와 미션을 고친다.
-- (가) 단계 안내(scenario_step)와 미션별 단계 안내(mission_step_guide)의 버튼 이름을 화면 글자 그대로 바꾼다.
--     AI 프롬프트의 "단계 안내"에 이 문구가 들어가므로 화면과 다르면 AI가 없는 버튼을 말하게 된다.
-- (나) 미션 102는 화면 표기(카페라떼)에 맞춘다.
-- (다) 미션 202는 화면에 라지 세트 버튼이 없어 성공할 수 없으므로 세트(MEAL_SET)로 바꾼다.

-- 1) 단계 안내
UPDATE scenario_step SET guide_message = '매장에서 드실지, 포장해 가실지 골라 주세요.', fallback_message = '매장에서 먹어요 또는 포장해서 갈래요 버튼을 눌러 보세요.' WHERE step_id = 11;
UPDATE scenario_step SET guide_message = '온도, 사이즈, 수량을 고른 뒤 선택 완료를 눌러 주세요.', fallback_message = '온도를 먼저 골라야 해요. HOT은 따뜻하게, ICE는 차갑게예요. 수량은 ＋ 버튼으로 늘려요.' WHERE step_id = 13;
UPDATE scenario_step SET guide_message = '더 담을 메뉴가 없으면 결제하기를 눌러 주세요.', fallback_message = '다른 메뉴가 필요하면 메뉴를 하나 더 눌러 담아요.' WHERE step_id = 14;
UPDATE scenario_step SET guide_message = '담은 메뉴와 수량을 확인하고 다음을 눌러 주세요.', fallback_message = '화면 아래 오른쪽 다음 버튼을 눌러 보세요.' WHERE step_id = 15;
UPDATE scenario_step SET guide_message = '멤버십이나 쿠폰이 없으면 적립·쿠폰 없이 진행을 눌러 주세요.', fallback_message = '적립·쿠폰 없이 진행 버튼을 눌러 보세요.' WHERE step_id = 16;
UPDATE scenario_step SET guide_message = '안내에 따라 결제를 마치고 결제 완료 확인을 눌러 주세요.', fallback_message = '카드를 끝까지 넣은 뒤 결제 완료 확인을 눌러 보세요.' WHERE step_id = 18;
UPDATE scenario_step SET guide_message = '대기번호를 확인하고 결제 완료 확인을 눌러 주세요.', fallback_message = '화면 아래 결제 완료 확인 버튼을 눌러 보세요.' WHERE step_id = 19;
UPDATE scenario_step SET guide_message = '매장에서 드실지, 포장해 가실지 골라 주세요.', fallback_message = '매장에서 먹기 또는 포장해서 가져가기 버튼을 눌러 보세요.' WHERE step_id = 21;
UPDATE scenario_step SET guide_message = '세트와 단품 중 미션에 맞는 구성을 골라 주세요.', fallback_message = '감자튀김과 음료가 함께 필요하면 세트를 눌러 보세요.' WHERE step_id = 23;
UPDATE scenario_step SET guide_message = '세트에 넣을 사이드와 음료를 고르고 선택 완료를 눌러 주세요.', fallback_message = '미션에 적힌 사이드와 음료를 하나씩 눌러 보세요.' WHERE step_id = 24;
UPDATE scenario_step SET guide_message = '더 고를 메뉴가 없으면 주문 내역 확인 후 결제하기를 눌러 주세요.', fallback_message = '화면 아래 주문 내역 확인 후 결제하기 버튼을 눌러 보세요.' WHERE step_id = 26;
UPDATE scenario_step SET guide_message = '주문 내역을 확인하고 주문 확인을 눌러 주세요.', fallback_message = '화면 아래 오른쪽 주문 확인 버튼을 눌러 보세요.' WHERE step_id = 27;
UPDATE scenario_step SET guide_message = '미션에 적힌 결제 방법을 골라 결제해 주세요.', fallback_message = '카드로 결제하려면 신용·체크카드를 누르고 카드를 넣어 보세요.' WHERE step_id = 28;
UPDATE scenario_step SET guide_message = '주문번호를 확인하고 주문번호 확인을 눌러 주세요.', fallback_message = '화면 아래 주문번호 확인 버튼을 눌러 보세요.' WHERE step_id = 29;

-- 1) 미션별 단계 안내
UPDATE mission_step_guide SET guide_message = '포장해 가실 거니까 포장해서 갈래요를 눌러 주세요.', highlight_target_code = 'ORDER_TAKE_OUT' WHERE mission_id = 101 AND step_id = 11;
UPDATE mission_step_guide SET guide_message = 'ICE와 Large를 고르고 ＋ 버튼으로 2잔을 만든 뒤 선택 완료를 눌러 주세요.', highlight_target_code = 'TEMP_ICE' WHERE mission_id = 101 AND step_id = 13;
UPDATE mission_step_guide SET guide_message = '더 담을 메뉴가 없으니 결제하기를 눌러 주세요.', highlight_target_code = 'BUTTON_GO_CART' WHERE mission_id = 101 AND step_id = 14;
UPDATE mission_step_guide SET guide_message = '아이스 아메리카노 Large 2잔이 맞는지 보고 다음을 눌러 주세요.', highlight_target_code = 'BUTTON_ORDER' WHERE mission_id = 101 AND step_id = 15;
UPDATE mission_step_guide SET guide_message = '적립·쿠폰 없이 진행을 눌러 주세요.', highlight_target_code = 'BUTTON_SKIP' WHERE mission_id = 101 AND step_id = 16;
UPDATE mission_step_guide SET guide_message = '카드를 끝까지 넣고 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'PAY_CARD' WHERE mission_id = 101 AND step_id = 18;
UPDATE mission_step_guide SET guide_message = '대기번호를 확인하고 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_DONE' WHERE mission_id = 101 AND step_id = 19;
UPDATE mission_step_guide SET guide_message = '매장에서 먹어요 또는 포장해서 갈래요 중 하나를 눌러 주세요.', highlight_target_code = 'ORDER_EAT_IN' WHERE mission_id = 102 AND step_id = 11;
UPDATE mission_step_guide SET guide_message = '카페라떼를 눌러 주세요.', highlight_target_code = 'MENU_CAFE_LATTE' WHERE mission_id = 102 AND step_id = 12;
UPDATE mission_step_guide SET guide_message = '따뜻한 음료니까 HOT을 고르고 선택 완료를 눌러 주세요.', highlight_target_code = 'TEMP_HOT' WHERE mission_id = 102 AND step_id = 13;
UPDATE mission_step_guide SET guide_message = '더 담을 메뉴가 없으니 결제하기를 눌러 주세요.', highlight_target_code = 'BUTTON_GO_CART' WHERE mission_id = 102 AND step_id = 14;
UPDATE mission_step_guide SET guide_message = '따뜻한 카페라떼 1잔이 맞는지 보고 다음을 눌러 주세요.', highlight_target_code = 'BUTTON_ORDER' WHERE mission_id = 102 AND step_id = 15;
UPDATE mission_step_guide SET guide_message = '적립·쿠폰 없이 진행을 눌러 주세요.', highlight_target_code = 'BUTTON_SKIP' WHERE mission_id = 102 AND step_id = 16;
UPDATE mission_step_guide SET guide_message = '카드를 끝까지 넣고 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'PAY_CARD' WHERE mission_id = 102 AND step_id = 18;
UPDATE mission_step_guide SET guide_message = '대기번호를 확인하고 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_DONE' WHERE mission_id = 102 AND step_id = 19;
UPDATE mission_step_guide SET guide_message = '매장에서 드실 거니까 매장에서 먹기를 눌러 주세요.', highlight_target_code = 'ORDER_EAT_IN' WHERE mission_id = 201 AND step_id = 21;
UPDATE mission_step_guide SET guide_message = '감자튀김과 콜라를 고르고 선택 완료를 눌러 주세요.', highlight_target_code = 'SIDE_FRIES' WHERE mission_id = 201 AND step_id = 24;
UPDATE mission_step_guide SET guide_message = '더 고를 메뉴가 없으니 주문 내역 확인 후 결제하기를 눌러 주세요.', highlight_target_code = 'BUTTON_GO_CART' WHERE mission_id = 201 AND step_id = 26;
UPDATE mission_step_guide SET guide_message = '불고기버거 세트 1개가 맞는지 보고 주문 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_ORDER' WHERE mission_id = 201 AND step_id = 27;
UPDATE mission_step_guide SET guide_message = '신용·체크카드를 누르고 카드를 넣은 뒤 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'PAY_CARD' WHERE mission_id = 201 AND step_id = 28;
UPDATE mission_step_guide SET guide_message = '주문번호를 확인하고 주문번호 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_DONE' WHERE mission_id = 201 AND step_id = 29;
UPDATE mission_step_guide SET guide_message = '매장에서 먹기 또는 포장해서 가져가기 중 하나를 눌러 주세요.', highlight_target_code = 'ORDER_EAT_IN' WHERE mission_id = 202 AND step_id = 21;
UPDATE mission_step_guide SET guide_message = '세트를 눌러 주세요.', highlight_target_code = 'MEAL_SET' WHERE mission_id = 202 AND step_id = 23;
UPDATE mission_step_guide SET guide_message = '감자튀김과 제로콜라를 고르고 선택 완료를 눌러 주세요.', highlight_target_code = 'DRINK_ZERO_COLA' WHERE mission_id = 202 AND step_id = 24;
UPDATE mission_step_guide SET guide_message = '더 고를 메뉴가 없으니 주문 내역 확인 후 결제하기를 눌러 주세요.', highlight_target_code = 'BUTTON_GO_CART' WHERE mission_id = 202 AND step_id = 26;
UPDATE mission_step_guide SET guide_message = '치킨버거 세트가 맞는지 보고 주문 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_ORDER' WHERE mission_id = 202 AND step_id = 27;
UPDATE mission_step_guide SET guide_message = '신용·체크카드를 누르고 카드를 넣은 뒤 결제 완료 확인을 눌러 주세요.', highlight_target_code = 'PAY_CARD' WHERE mission_id = 202 AND step_id = 28;
UPDATE mission_step_guide SET guide_message = '주문번호를 확인하고 주문번호 확인을 눌러 주세요.', highlight_target_code = 'BUTTON_DONE' WHERE mission_id = 202 AND step_id = 29;
-- 2) 미션 102
UPDATE mission
SET title = '따뜻한 카페라떼 주문하기',
    description = '① 따뜻한 카페라떼 1잔 담기\n② 카드로 결제하기',
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_EAT_IN"},{"action_type":"SELECT","target_code":"ORDER_TAKE_OUT"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_CAFE_LATTE"}]},"SELECT_OPTION":{"accept":[{"action_type":"OPTION","target_code":"TEMP_HOT"},{"action_type":"OPTION","target_code":"SIZE_REGULAR"},{"action_type":"OPTION","target_code":"SIZE_LARGE"},{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"ADD_MORE_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"MEMBERSHIP":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_SKIP"}]},"SELECT_PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"}]},"PAYMENT":{"accept":[{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"MENU","label":"따뜻한 카페라떼 1잔","source":"CART_ITEM","step_code":"CART","action_type":"NEXT","menu":"MENU_CAFE_LATTE","expected":{"temperature":"TEMP_HOT","quantity":1},"quantity_unit":"잔"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 102;

-- 3) 미션 202
UPDATE mission
SET title = '치킨버거 세트 주문하기',
    description = '① 치킨버거 세트 선택\n② 감자튀김과 제로콜라 선택\n③ 카드로 결제하기',
    target_data = '{"SELECT_ORDER_TYPE":{"accept":[{"action_type":"SELECT","target_code":"ORDER_EAT_IN"},{"action_type":"SELECT","target_code":"ORDER_TAKE_OUT"}]},"SELECT_MENU":{"accept":[{"action_type":"SELECT","target_code":"MENU_CHICKEN_BURGER"}]},"SELECT_MEAL_TYPE":{"accept":[{"action_type":"OPTION","target_code":"MEAL_SET"}]},"SELECT_SIDE_DRINK":{"accept":[{"action_type":"OPTION","target_code":"SIDE_FRIES"},{"action_type":"OPTION","target_code":"DRINK_ZERO_COLA"},{"action_type":"NEXT","target_code":"BUTTON_NEXT"}]},"ADD_TO_CART":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ADD_TO_CART"}]},"SELECT_EXTRA_MENU":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_GO_CART"}]},"ORDER_REVIEW":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_ORDER"}]},"PAYMENT":{"accept":[{"action_type":"SELECT","target_code":"PAY_CARD"},{"action_type":"PAY","target_code":"PAY_CARD"}]},"ORDER_COMPLETE":{"accept":[{"action_type":"NEXT","target_code":"BUTTON_DONE"}]},"conditions":[{"code":"MENU","label":"치킨버거 세트 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_CHICKEN_BURGER","expected":{"meal_type":"MEAL_SET","quantity":1},"quantity_unit":"개"},{"code":"SIDE_DRINK","label":"감자튀김 · 제로콜라 선택","source":"CART_ITEM","step_code":"ORDER_REVIEW","action_type":"NEXT","menu":"MENU_CHICKEN_BURGER","expected":{"side":"SIDE_FRIES","drink":"DRINK_ZERO_COLA"},"quantity_unit":"개"},{"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY","expected_target_code":"PAY_CARD"}]}'
WHERE mission_id = 202;
