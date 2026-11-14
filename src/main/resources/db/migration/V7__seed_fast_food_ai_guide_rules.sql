INSERT INTO ai_guide_rule
    (scenario_type, screen_code, intent_code, keywords, example_phrases, response_template, priority, is_active)
VALUES
-- 모든 패스트푸드 화면 공통
('FAST_FOOD', 'COMMON', 'ASK_CURRENT_TASK', '뭐 해야, 다음, 어떻게, 모르겠어', '지금 뭘 해야 해요? | 다음에는 뭐 해요?', '현재 화면의 안내와 오늘의 미션을 확인하고, 안내된 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'COMMON', 'CANNOT_FIND', '없어, 안 보여, 못 찾겠어, 어디', '버튼이 안 보여요 | 어디 있는지 모르겠어요', '현재 화면에서 안내받은 이름과 같은 버튼을 천천히 찾아 직접 눌러 주세요. 찾기 어렵다면 버튼 이름을 더 정확히 말해 주세요.', 20, TRUE),
('FAST_FOOD', 'COMMON', 'GO_BACK', '이전, 뒤로, 돌아가, 잘못', '이전 화면으로 가고 싶어요 | 잘못 눌렀어요', '화면 위쪽의 이전으로 또는 뒤로 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'COMMON', 'GO_HOME', '처음, 홈, 다시 시작', '처음부터 다시 할래요 | 홈으로 갈래요', '화면 위쪽의 처음으로 또는 홈 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'COMMON', 'REPEAT_GUIDE', '다시, 못 들었어, 한 번 더', '다시 말해 주세요 | 못 들었어요', '현재 화면에서 눌러야 할 버튼을 다시 한 번 짧게 안내해 주세요.', 50, TRUE),
('FAST_FOOD', 'COMMON', 'AMBIGUOUS_REQUEST', '이거, 저거, 그거, 해줘', '이걸로 해 줘요 | 저거 고를래요', '어떤 메뉴나 버튼을 말하는지 이름을 조금 더 정확히 말해 주세요.', 60, TRUE),
('FAST_FOOD', 'COMMON', 'REQUEST_AUTOMATION', '대신, 눌러 줘, 주문해 줘, 결제해 줘', '대신 주문해 주세요 | 눌러 주세요', '제가 대신 누르거나 주문할 수는 없습니다. 원하는 항목의 버튼을 직접 눌러 주세요.', 70, TRUE),
('FAST_FOOD', 'COMMON', 'WRONG_SELECTION', '잘못, 바꿀래, 틀렸어', '잘못 골랐어요 | 선택을 바꾸고 싶어요', '이전으로 버튼이나 해당 메뉴의 수정 버튼을 직접 눌러 선택을 바꿔 주세요.', 80, TRUE),
('FAST_FOOD', 'COMMON', 'OUT_OF_SCOPE', '날씨, 뉴스, 전화, 게임', '오늘 날씨 알려 줘요 | 전화해 줘요', '지금은 패스트푸드 주문의 현재 단계에 관한 질문만 도와드릴 수 있습니다.', 90, TRUE),
('FAST_FOOD', 'COMMON', 'PROTECT_PRIVATE_DATA', '카드번호, 비밀번호, 주민번호', '카드 비밀번호를 말하면 돼요?', '비밀번호나 카드번호는 말하지 말고 결제 단말기에 직접 입력해 주세요.', 100, TRUE),

-- 오늘의 미션
('FAST_FOOD', 'MISSION_BRIEFING', 'ASK_MISSION', '미션, 주문할 것, 뭘 주문', '오늘 뭘 주문해야 해요? | 미션이 뭐예요?', '오늘의 미션에 적힌 메뉴, 단품 또는 세트, 사이드, 음료, 수량과 결제 조건을 확인해 주세요.', 10, TRUE),
('FAST_FOOD', 'MISSION_BRIEFING', 'REPEAT_MISSION', '미션 다시, 조건, 뭐였지', '미션을 다시 알려 주세요 | 조건이 뭐였어요?', '화면의 오늘의 미션을 위에서부터 천천히 확인해 주세요. 메뉴와 옵션 조건을 빠뜨리지 않도록 주의해 주세요.', 20, TRUE),
('FAST_FOOD', 'MISSION_BRIEFING', 'START_MISSION', '시작, 주문 시작, 해볼게', '이제 시작할게요 | 주문을 시작할래요', '주문 시작 버튼을 직접 눌러 주세요.', 30, TRUE),

-- 매장/포장
('FAST_FOOD', 'SELECT_ORDER_TYPE', 'SELECT_DINE_IN', '매장, 여기서, 먹고 갈게', '매장에서 먹을게요 | 여기서 먹어요', '매장에서 먹기 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_ORDER_TYPE', 'SELECT_TAKEOUT', '포장, 가져갈게, 테이크아웃', '포장할게요 | 가져갈 거예요', '포장하기 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_ORDER_TYPE', 'ASK_DIFFERENCE', '차이, 매장과 포장, 뭐가 달라', '매장과 포장이 뭐가 달라요?', '매장에서 드시면 매장에서 먹기, 가져가시면 포장하기 버튼을 직접 눌러 주세요.', 30, TRUE),

-- 메뉴 카테고리
('FAST_FOOD', 'SELECT_CATEGORY', 'SELECT_BURGER_CATEGORY', '버거, 햄버거', '버거는 어디 있어요?', '메뉴 분류에서 버거 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_CATEGORY', 'FIND_SET_MENU', '세트, 세트 메뉴', '세트 메뉴를 보고 싶어요', '메뉴 분류에서 버거 버튼을 직접 눌러 주세요. 단품과 세트는 메뉴를 고른 뒤 선택할 수 있습니다.', 20, TRUE),
('FAST_FOOD', 'SELECT_CATEGORY', 'SELECT_SIDE_CATEGORY', '감자튀김, 사이드, 치킨', '사이드 메뉴는 어디예요?', '메뉴 분류에서 사이드 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_CATEGORY', 'SELECT_DRINK_CATEGORY', '음료, 콜라, 주스', '음료는 어디에 있어요?', '메뉴 분류에서 음료 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_CATEGORY', 'SELECT_DESSERT_CATEGORY', '디저트, 아이스크림', '디저트 메뉴를 보고 싶어요', '메뉴 분류에서 디저트 버튼을 직접 눌러 주세요.', 50, TRUE),
('FAST_FOOD', 'SELECT_CATEGORY', 'CHANGE_CATEGORY', '다른 메뉴, 종류 변경, 카테고리', '다른 종류를 보고 싶어요', '화면의 메뉴 분류에서 원하는 종류의 버튼을 직접 눌러 주세요.', 60, TRUE),

-- 메뉴 선택
('FAST_FOOD', 'SELECT_MENU', 'FIND_MISSION_MENU', '미션 메뉴, 무엇, 어디, 찾아', '미션에서 주문하라는 버거가 어디 있어요? | 무엇을 골라요?', '오늘의 미션에 적힌 메뉴와 같은 이름의 메뉴 버튼을 찾아 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_MENU', 'SELECT_NAMED_MENU', '불고기버거, 치즈버거, 새우버거, 치킨버거', '불고기버거로 할래요 | 치즈버거를 고를래요', '말씀하신 메뉴와 같은 이름의 메뉴 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_MENU', 'AMBIGUOUS_MENU', '이 버거, 첫 번째, 저거', '저 버거로 할래요 | 첫 번째 걸로 해 줘요', '어떤 버거인지 메뉴 이름을 조금 더 정확히 말해 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_MENU', 'ASK_MENU_DETAIL', '가격, 재료, 어떤 메뉴', '이 버거는 어떤 버거예요? | 가격이 얼마예요?', '메뉴 버튼에 표시된 이름, 사진과 가격을 확인해 주세요. 제공되지 않은 재료 정보는 직원에게 문의해 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_MENU', 'MENU_SOLD_OUT', '품절, 선택 안 돼, 없어', '메뉴를 누를 수 없어요 | 품절이에요', '품절 표시가 있다면 선택할 수 없습니다. 미션 메뉴가 품절이면 직원에게 문의해 주세요.', 50, TRUE),
('FAST_FOOD', 'SELECT_MENU', 'BROWSE_MORE_MENU', '다른 버거, 더 보기, 다른 메뉴', '다른 버거도 보고 싶어요', '메뉴 목록을 살펴보거나 원하는 카테고리 버튼을 직접 눌러 주세요.', 60, TRUE),

-- 단품/세트
('FAST_FOOD', 'SELECT_MEAL_TYPE', 'SELECT_SINGLE', '단품, 버거만, 하나만', '버거만 주문할래요 | 단품으로 할래요', '단품 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_MEAL_TYPE', 'SELECT_SET', '세트, 감자튀김과 음료', '세트로 할래요', '세트 메뉴 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_MEAL_TYPE', 'ASK_DIFFERENCE', '차이, 뭐가 달라, 단품과 세트', '단품과 세트가 뭐가 달라요?', '단품은 선택한 메뉴만 주문하고, 세트는 사이드와 음료를 함께 선택합니다. 원하는 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_MEAL_TYPE', 'ASK_MISSION_MEAL_TYPE', '미션 단품, 미션 세트, 뭘 골라', '미션에서는 뭘 골라야 해요?', '오늘의 미션에 적힌 단품 또는 세트 조건과 같은 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_MEAL_TYPE', 'ASK_SET_CONTENTS', '세트 구성, 뭐 들어가', '세트에는 무엇이 들어가요?', '세트를 선택하면 다음 화면에서 사이드와 음료를 직접 고를 수 있습니다.', 50, TRUE),

-- 세트 크기
('FAST_FOOD', 'SELECT_SET_SIZE', 'SELECT_REGULAR_SET', '기본, 보통, 일반, 레귤러', '기본 세트로 할래요', '기본 세트 또는 레귤러 세트 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_SET_SIZE', 'SELECT_LARGE_SET', '라지, 큰 세트, 크게', '큰 세트로 바꿀래요', '라지 세트 또는 큰 세트 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_SET_SIZE', 'ASK_SIZE_DIFFERENCE', '크기 차이, 뭐가 달라', '기본 세트와 큰 세트가 뭐가 달라요?', '큰 세트는 사이드와 음료 크기가 커질 수 있습니다. 화면의 추가 금액을 확인하고 원하는 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_SET_SIZE', 'ASK_MISSION_SIZE', '미션 크기, 어떤 크기', '미션에서는 어떤 크기예요?', '오늘의 미션에 적힌 세트 크기와 같은 버튼을 직접 눌러 주세요.', 40, TRUE),

-- 사이드
('FAST_FOOD', 'SELECT_SIDE', 'SELECT_FRIES', '감자튀김, 프렌치프라이', '감자튀김으로 할래요', '감자튀김 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_SIDE', 'SELECT_NAMED_SIDE', '치즈스틱, 너겟, 샐러드, 다른 사이드', '치즈스틱으로 바꿀래요', '원하는 사이드 메뉴와 같은 이름의 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_SIDE', 'KEEP_DEFAULT_SIDE', '기본, 그대로, 변경 안 해', '기본으로 할게요', '기본으로 표시된 사이드를 확인하고 다음 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_SIDE', 'ASK_SIDE_PRICE', '추가 금액, 얼마 더, 가격', '바꾸면 돈이 더 들어요?', '각 사이드 버튼에 표시된 추가 금액을 확인한 뒤 원하는 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_SIDE', 'ASK_MISSION_SIDE', '미션 사이드, 뭘 골라', '어떤 사이드를 골라야 해요?', '오늘의 미션에 적힌 사이드와 같은 이름의 버튼을 직접 눌러 주세요.', 50, TRUE),
('FAST_FOOD', 'SELECT_SIDE', 'CONFIRM_SIDE', '다 골랐어, 다음, 선택 완료', '감자튀김을 골랐어요', '선택된 사이드를 확인한 뒤 다음 버튼을 직접 눌러 주세요.', 60, TRUE),

-- 음료
('FAST_FOOD', 'SELECT_DRINK', 'SELECT_COLA', '콜라, 코카콜라', '콜라로 할래요', '콜라 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'SELECT_ZERO_DRINK', '제로, 무설탕', '제로 콜라로 바꿀래요', '제로 음료와 같은 이름의 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'SELECT_NAMED_DRINK', '사이다, 주스, 커피, 다른 음료', '사이다로 할래요', '원하는 음료와 같은 이름의 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'KEEP_DEFAULT_DRINK', '기본, 그대로, 변경 안 해', '기본 음료로 할게요', '기본으로 표시된 음료를 확인한 뒤 다음 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'ASK_DRINK_PRICE', '추가 금액, 돈 더, 가격', '음료를 바꾸면 추가 금액이 있어요?', '음료 버튼에 표시된 추가 금액을 확인한 뒤 원하는 버튼을 직접 눌러 주세요.', 50, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'ASK_MISSION_DRINK', '미션 음료, 무엇, 뭘 골라', '어떤 음료를 골라요?', '오늘의 미션에 적힌 음료와 같은 이름의 버튼을 직접 눌러 주세요.', 60, TRUE),
('FAST_FOOD', 'SELECT_DRINK', 'CONFIRM_DRINK', '다 골랐어, 다음, 선택 완료', '음료를 골랐어요', '선택된 음료를 확인한 뒤 다음 버튼을 직접 눌러 주세요.', 70, TRUE),

-- 재료와 추가 옵션
('FAST_FOOD', 'SELECT_OPTION', 'ADD_INGREDIENT', '치즈 추가, 패티 추가, 소스 추가, 재료 추가', '치즈를 추가하고 싶어요', '원하는 추가 재료와 같은 이름의 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_OPTION', 'REMOVE_INGREDIENT', '빼 주세요, 양파 빼, 피클 빼, 재료 제외', '피클을 빼고 싶어요', '제외할 수 있는 항목이라면 해당 재료의 제외 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_OPTION', 'KEEP_DEFAULT_OPTION', '기본, 그대로, 추가 안 해', '아무것도 추가하지 않을게요', '기본 상태를 유지하고 선택 완료 또는 다음 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_OPTION', 'ASK_OPTION_PRICE', '얼마, 추가 금액, 가격', '치즈를 추가하면 얼마예요?', '옵션 옆에 표시된 추가 금액을 확인해 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_OPTION', 'RESET_OPTION', '취소, 원래대로, 초기화', '옵션을 원래대로 돌릴래요', '선택한 옵션을 다시 누르거나 초기화 버튼을 직접 눌러 주세요.', 50, TRUE),
('FAST_FOOD', 'SELECT_OPTION', 'CONFIRM_OPTION', '완료, 다 골랐어, 다음', '옵션을 다 골랐어요', '선택한 옵션을 확인한 뒤 선택 완료 또는 다음 버튼을 직접 눌러 주세요.', 60, TRUE),

-- 수량
('FAST_FOOD', 'SELECT_QUANTITY', 'INCREASE_QUANTITY', '더, 추가, 두 개, 수량 늘려', '두 개 주문할래요 | 하나 더 추가해요', '더하기 버튼을 필요한 수량이 될 때까지 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_QUANTITY', 'DECREASE_QUANTITY', '빼, 줄여, 한 개, 수량 줄여', '한 개로 줄일래요', '빼기 버튼을 필요한 수량이 될 때까지 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_QUANTITY', 'ASK_CURRENT_QUANTITY', '몇 개, 수량, 지금', '지금 몇 개예요?', '화면에 표시된 수량 숫자를 확인해 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_QUANTITY', 'ASK_MISSION_QUANTITY', '미션 수량, 몇 개 주문', '몇 개를 주문해야 해요?', '오늘의 미션에 적힌 수량이 되도록 더하기 또는 빼기 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'SELECT_QUANTITY', 'ADD_TO_CART', '담기, 장바구니, 이대로', '이대로 담을래요', '메뉴, 옵션과 수량을 확인한 뒤 장바구니 담기 버튼을 직접 눌러 주세요.', 50, TRUE),

-- 추가 메뉴 추천
('FAST_FOOD', 'SELECT_EXTRA_MENU', 'SELECT_EXTRA', '추가, 하나 더, 사이드 추가', '너겟도 추가할래요', '원하는 추가 메뉴와 같은 이름의 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_EXTRA_MENU', 'SKIP_EXTRA', '필요 없어, 안 할래, 건너뛰기', '추가 메뉴는 필요 없어요', '추가 안 함 또는 건너뛰기 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_EXTRA_MENU', 'ASK_OUTSIDE_MISSION', '미션에 없어, 추가해도 돼', '미션에 없는 메뉴를 추가해도 돼요?', '미션을 정확히 수행하려면 미션에 적힌 메뉴와 조건만 선택해 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_EXTRA_MENU', 'ASK_EXTRA_PRICE', '가격, 얼마', '이 메뉴는 얼마예요?', '메뉴 버튼에 표시된 가격을 확인해 주세요.', 40, TRUE),

-- 장바구니
('FAST_FOOD', 'CART', 'REVIEW_CART', '뭐 담겼어, 주문 확인, 장바구니', '제가 무엇을 골랐어요?', '장바구니에 표시된 메뉴 이름, 단품 또는 세트, 사이드, 음료, 수량과 금액을 확인해 주세요.', 10, TRUE),
('FAST_FOOD', 'CART', 'EDIT_ITEM', '수정, 바꾸기, 옵션 변경', '음료를 바꾸고 싶어요', '변경할 메뉴의 수정 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'CART', 'DELETE_ITEM', '삭제, 빼기, 취소', '이 버거를 빼고 싶어요', '삭제할 메뉴의 삭제 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'CART', 'ADD_MORE_ITEM', '더 주문, 메뉴 추가, 더 담기', '메뉴를 하나 더 고를래요', '메뉴 추가 또는 더 담기 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'CART', 'PROCEED_ORDER', '주문, 결제, 다음', '이제 결제할래요', '주문 내용과 총금액을 확인한 뒤 주문하기 또는 결제하기 버튼을 직접 눌러 주세요.', 50, TRUE),

-- 최종 주문 확인
('FAST_FOOD', 'ORDER_REVIEW', 'CONFIRM_ORDER', '맞아, 그대로, 주문할게', '주문 내용이 맞아요', '메뉴, 옵션, 수량과 금액을 확인한 뒤 다음 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'ORDER_REVIEW', 'ORDER_MISMATCH', '틀려, 달라, 잘못, 수정', '음료가 잘못됐어요', '이전으로 또는 수정 버튼을 직접 눌러 잘못된 항목을 변경해 주세요.', 20, TRUE),
('FAST_FOOD', 'ORDER_REVIEW', 'ASK_TOTAL_PRICE', '총액, 얼마, 가격', '모두 얼마예요?', '화면 아래쪽에 표시된 총 주문 금액을 확인해 주세요.', 30, TRUE),
('FAST_FOOD', 'ORDER_REVIEW', 'PROCEED_PAYMENT', '결제할래, 다음', '이제 결제하면 돼요?', '주문 내용이 맞다면 결제하기 또는 다음 버튼을 직접 눌러 주세요.', 40, TRUE),

-- 쿠폰/포인트
('FAST_FOOD', 'MEMBERSHIP', 'SKIP_MEMBERSHIP', '적립 안 해, 회원 아냐, 건너뛰기', '적립하지 않을게요', '적립 안 함 또는 건너뛰기 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'MEMBERSHIP', 'USE_MEMBERSHIP', '적립, 회원, 포인트', '포인트를 적립할래요', '포인트 적립 버튼을 직접 누른 뒤 화면 안내에 따라 정보를 직접 입력해 주세요.', 20, TRUE),
('FAST_FOOD', 'MEMBERSHIP', 'USE_COUPON', '쿠폰, 할인', '쿠폰을 사용할래요', '쿠폰 사용 버튼을 직접 누른 뒤 쿠폰 번호나 바코드를 화면 안내에 따라 사용해 주세요.', 30, TRUE),
('FAST_FOOD', 'MEMBERSHIP', 'NO_COUPON', '쿠폰 없어, 없음', '쿠폰이 없어요', '쿠폰 없음 또는 건너뛰기 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'MEMBERSHIP', 'ENTER_PHONE_NUMBER', '전화번호, 번호 입력', '전화번호는 어디에 입력해요?', '화면의 입력 칸에 전화번호를 직접 입력해 주세요. 번호를 음성으로 말하지 마세요.', 50, TRUE),

-- 결제 방법
('FAST_FOOD', 'SELECT_PAYMENT', 'SELECT_CARD', '카드, 신용카드, 체크카드', '카드로 결제할래요', '카드 결제 버튼을 직접 눌러 주세요.', 10, TRUE),
('FAST_FOOD', 'SELECT_PAYMENT', 'SELECT_MOBILE', '휴대폰, 모바일, 삼성페이, 애플페이', '휴대폰으로 결제할래요', '모바일 결제 버튼을 직접 눌러 주세요.', 20, TRUE),
('FAST_FOOD', 'SELECT_PAYMENT', 'SELECT_BARCODE', '바코드, QR, 큐알, 간편결제', '바코드로 결제할래요', '바코드 또는 간편결제 버튼을 직접 눌러 주세요.', 30, TRUE),
('FAST_FOOD', 'SELECT_PAYMENT', 'ASK_PAYMENT_DIFFERENCE', '뭐가 달라, 어떤 결제, 결제 방법', '어떤 결제 방법을 골라야 해요?', '사용할 수단과 같은 결제 방법 버튼을 직접 눌러 주세요. 카드가 있으면 카드 결제를 선택할 수 있습니다.', 40, TRUE),
('FAST_FOOD', 'SELECT_PAYMENT', 'ASK_MISSION_PAYMENT', '미션 결제, 무엇으로 결제', '미션에서는 어떻게 결제해요?', '오늘의 미션에 적힌 결제 방법과 같은 버튼을 직접 눌러 주세요.', 50, TRUE),

-- 카드 결제
('FAST_FOOD', 'PAY_CARD', 'INSERT_CARD', '카드 어디, 넣어, 꽂아, 투입', '카드를 어디에 넣어요?', '카드 결제기의 카드 투입구에 카드를 직접 넣어 주세요.', 10, TRUE),
('FAST_FOOD', 'PAY_CARD', 'PAYMENT_WAIT', '처리 중, 안 넘어가, 기다려', '결제 중인데 화면이 안 넘어가요', '카드를 빼지 말고 결제 완료 또는 오류 안내가 나올 때까지 잠시 기다려 주세요.', 20, TRUE),
('FAST_FOOD', 'PAY_CARD', 'REMOVE_CARD', '카드 빼, 빼도 돼', '카드를 빼도 돼요?', '화면이나 결제 단말기에 카드를 빼 달라는 안내가 나온 뒤 직접 빼 주세요.', 30, TRUE),
('FAST_FOOD', 'PAY_CARD', 'PAYMENT_ERROR', '실패, 오류, 안 돼', '카드 결제가 안 돼요', '화면의 오류 안내를 확인하고 다시 시도하거나 다른 결제 방법을 선택해 주세요.', 40, TRUE),
('FAST_FOOD', 'PAY_CARD', 'ENTER_PASSWORD', '비밀번호, 번호', '비밀번호를 말하면 돼요?', '비밀번호는 말하지 말고 결제 단말기에 직접 입력해 주세요.', 50, TRUE),

-- 모바일 결제
('FAST_FOOD', 'PAY_MOBILE', 'TAG_MOBILE', '휴대폰 어디, 태그, 대', '휴대폰을 어디에 대요?', '결제 단말기의 표시된 위치에 휴대폰을 직접 가까이 대 주세요.', 10, TRUE),
('FAST_FOOD', 'PAY_MOBILE', 'OPEN_PAYMENT_APP', '앱, 삼성페이, 애플페이', '결제 앱을 먼저 열어야 해요?', '휴대폰에서 사용할 결제 앱을 직접 연 뒤 결제 단말기에 가까이 대 주세요.', 20, TRUE),
('FAST_FOOD', 'PAY_MOBILE', 'MOBILE_NOT_RECOGNIZED', '인식 안 돼, 실패', '휴대폰을 인식하지 못해요', '휴대폰 화면의 결제 준비 상태를 확인하고 단말기에 다시 가까이 대 주세요.', 30, TRUE),

-- 바코드/QR 결제
('FAST_FOOD', 'PAY_BARCODE', 'SHOW_BARCODE', '바코드 어디, 보여 줘', '바코드를 어디에 보여 줘요?', '휴대폰에서 결제 바코드를 직접 열고 스캐너에 보여 주세요.', 10, TRUE),
('FAST_FOOD', 'PAY_BARCODE', 'SHOW_QR', 'QR, 큐알', 'QR 화면을 열었어요', '휴대폰의 QR 코드를 스캐너에 직접 보여 주세요.', 20, TRUE),
('FAST_FOOD', 'PAY_BARCODE', 'BARCODE_NOT_RECOGNIZED', '스캔 안 돼, 인식 안 돼', '바코드가 인식되지 않아요', '화면 밝기를 높이고 바코드를 스캐너 앞에 다시 보여 주세요.', 30, TRUE),

-- 주문 완료
('FAST_FOOD', 'ORDER_COMPLETE', 'CHECK_ORDER_NUMBER', '번호, 주문 번호, 몇 번', '제 주문 번호가 뭐예요?', '완료 화면에 크게 표시된 주문 번호를 확인해 주세요.', 10, TRUE),
('FAST_FOOD', 'ORDER_COMPLETE', 'CHECK_COMPLETION', '끝, 완료됐어', '주문이 끝난 건가요?', '주문 완료 화면과 주문 번호가 표시되었다면 결제가 완료된 상태입니다.', 20, TRUE),
('FAST_FOOD', 'ORDER_COMPLETE', 'ASK_PICKUP', '어디서 받아, 언제 나와, 수령', '음식은 어디서 받아요?', '주문 번호를 기억하고, 매장의 주문 수령 화면이나 직원 안내를 확인해 주세요.', 30, TRUE),
('FAST_FOOD', 'ORDER_COMPLETE', 'FINISH', '끝내기, 나가기, 완료', '이제 어떻게 끝내요?', '주문 번호를 확인한 뒤 체험 완료 버튼을 직접 눌러 주세요.', 40, TRUE),
('FAST_FOOD', 'ORDER_COMPLETE', 'RETRY', '다시, 한 번 더, 재도전', '다시 해 보고 싶어요', '다시 도전 버튼을 직접 눌러 주세요.', 50, TRUE);
