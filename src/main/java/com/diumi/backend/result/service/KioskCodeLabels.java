package com.diumi.backend.result.service;

import java.util.Map;

/**
 * 키오스크 버튼 코드를 결과 화면에 보여 줄 한국어 이름으로 바꾼다.
 * 목록에 없는 코드는 코드 그대로 보여 준다.
 */
final class KioskCodeLabels {
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("ORDER_EAT_IN", "매장 식사"),
            Map.entry("ORDER_TAKE_OUT", "포장"),
            Map.entry("MENU_AMERICANO", "아메리카노"),
            Map.entry("MENU_CAFE_LATTE", "카페라떼"),
            Map.entry("MENU_CAFE_MOCHA", "카페모카"),
            Map.entry("MENU_COLD_BREW", "콜드브루"),
            Map.entry("TEMP_HOT", "따뜻하게(HOT)"),
            Map.entry("TEMP_ICE", "차갑게(ICE)"),
            Map.entry("SIZE_REGULAR", "Regular"),
            Map.entry("SIZE_LARGE", "Large"),
            Map.entry("SIZE_EXTRA", "Extra"),
            Map.entry("MENU_BULGOGI_BURGER", "불고기버거"),
            Map.entry("MENU_CHICKEN_BURGER", "치킨버거"),
            Map.entry("MENU_CHEESE_BURGER", "치즈버거"),
            Map.entry("MENU_SPICY_BURGER", "매운버거"),
            Map.entry("MEAL_SINGLE", "단품"),
            Map.entry("MEAL_SET", "세트"),
            Map.entry("MEAL_LARGE_SET", "라지 세트"),
            Map.entry("SIDE_FRIES", "감자튀김"),
            Map.entry("SIDE_CHEESE_STICK", "치즈스틱"),
            Map.entry("SIDE_SALAD", "샐러드"),
            Map.entry("SIDE_NUGGETS", "치킨너겟"),
            Map.entry("DRINK_COLA", "콜라"),
            Map.entry("DRINK_ZERO_COLA", "제로콜라"),
            Map.entry("DRINK_CIDER", "사이다"),
            Map.entry("DRINK_ORANGE", "오렌지"),
            Map.entry("PAY_CARD", "신용·체크카드"),
            Map.entry("PAY_MOBILE", "모바일페이"),
            Map.entry("PAY_GIFT_CARD", "기프트카드")
    );

    private KioskCodeLabels() {
    }

    static String label(String code) {
        return code == null ? null : LABELS.getOrDefault(code, code);
    }

    /** 앞 단어의 받침에 맞춰 '을' 또는 '를'을 붙인다. 한글이 아니면 '를'을 붙인다. */
    static String withObjectParticle(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        char last = word.charAt(word.length() - 1);
        if (last >= 0xAC00 && last <= 0xD7A3) {
            return word + (((last - 0xAC00) % 28) == 0 ? "를" : "을");
        }
        return word + "를";
    }
}
