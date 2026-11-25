package com.diumi.backend.ai.screen.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 화면에 있는 버튼 하나.
 * button_code는 프론트가 행동 기록에 보내는 target_code와 같은 값이다(예: MENU_AMERICANO).
 * 행동 기록 코드가 없는 버튼은 button_code를 비워 둔다.
 */
@Entity
@Table(name = "ai_screen_button")
public class AiScreenButton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "button_id")
    private Long id;

    @Column(name = "screen_id", nullable = false)
    private Long screenId;

    @Column(name = "button_code", length = 50)
    private String buttonCode;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    protected AiScreenButton() {
    }

    public AiScreenButton(Long screenId, String buttonCode, String label,
                          String description, int displayOrder) {
        this.screenId = screenId;
        this.buttonCode = buttonCode;
        this.label = label;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public String getButtonCode() { return buttonCode; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }
}
