-- 기본 콘텐츠(시드 데이터)
-- 카페 시나리오만 단계와 미션까지 채워 두었습니다.
-- 나머지 시나리오는 단계·미션이 정해지면 추가하고 is_active를 TRUE로 바꾸는 새 마이그레이션을 만듭니다.

INSERT INTO scenario (scenario_id, scenario_type, title, description, thumbnail_url, is_active) VALUES
    (1, 'CAFE', '카페', '카페 키오스크로 음료를 주문해 봐요', '/images/scenario/cafe.png', TRUE),
    (2, 'FAST_FOOD', '패스트푸드', '햄버거 세트를 주문해 봐요', '/images/scenario/fast-food.png', FALSE),
    (3, 'TRAIN', '열차', '열차표를 예매해 봐요', '/images/scenario/train.png', FALSE),
    (4, 'NAVIGATION', '길찾기', '지도 앱으로 길을 찾아봐요', '/images/scenario/navigation.png', FALSE);

INSERT INTO scenario_step (step_id, scenario_id, step_code, step_order, step_name, guide_message, fallback_message) VALUES
    (11, 1, 'SELECT_MENU', 1, '메뉴 고르기', '마시고 싶은 음료를 눌러 주세요.', '화면에서 음료 사진을 눌러 보세요.'),
    (12, 1, 'SELECT_OPTION', 2, '옵션 고르기', '따뜻한 음료와 차가운 음료 중에 골라 주세요.', '뜨거운 것은 HOT, 차가운 것은 ICE 버튼이에요.'),
    (13, 1, 'CONFIRM_ORDER', 3, '주문 확인', '담은 메뉴를 확인하고 주문하기를 눌러 주세요.', '화면 아래 주문하기 버튼을 눌러 보세요.'),
    (14, 1, 'PAYMENT', 4, '결제하기', '결제 방법을 골라 주세요.', '카드로 결제하려면 카드 결제 버튼을 눌러 보세요.');

INSERT INTO mission (mission_id, scenario_id, title, description, difficulty, target_data, is_active, estimated_minutes) VALUES
    (101, 1, '아이스 아메리카노 주문하기', '① 아이스 아메리카노 1잔 담기\n② 카드로 결제하기', 'EASY',
     '{"SELECT_MENU": {"action_type": "SELECT", "target_code": "MENU_AMERICANO"}, "SELECT_OPTION": {"action_type": "OPTION", "target_code": "OPTION_ICE"}, "CONFIRM_ORDER": {"action_type": "NEXT"}, "PAYMENT": {"action_type": "PAY", "target_code": "PAY_CARD"}}',
     TRUE, 3),
    (102, 1, '따뜻한 카페라테 주문하기', '① 따뜻한 카페라테 1잔 담기\n② 카드로 결제하기', 'NORMAL',
     '{"SELECT_MENU": {"action_type": "SELECT", "target_code": "MENU_CAFE_LATTE"}, "SELECT_OPTION": {"action_type": "OPTION", "target_code": "OPTION_HOT"}, "CONFIRM_ORDER": {"action_type": "NEXT"}, "PAYMENT": {"action_type": "PAY", "target_code": "PAY_CARD"}}',
     TRUE, 4);

INSERT INTO mission_step_guide (mission_id, step_id, guide_message, highlight_target_code) VALUES
    (101, 11, '아메리카노를 눌러 주세요.', 'MENU_AMERICANO'),
    (101, 12, '차가운 음료를 원하시니 ICE를 눌러 주세요.', 'OPTION_ICE'),
    (101, 13, '주문하기 버튼을 눌러 주세요.', 'BUTTON_NEXT'),
    (101, 14, '카드 결제를 눌러 주세요.', 'PAY_CARD'),
    (102, 11, '카페라테를 눌러 주세요.', 'MENU_CAFE_LATTE'),
    (102, 12, '따뜻한 음료를 원하시니 HOT을 눌러 주세요.', 'OPTION_HOT'),
    (102, 13, '주문하기 버튼을 눌러 주세요.', 'BUTTON_NEXT'),
    (102, 14, '카드 결제를 눌러 주세요.', 'PAY_CARD');
