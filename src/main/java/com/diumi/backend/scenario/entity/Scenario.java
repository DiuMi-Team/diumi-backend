package com.diumi.backend.scenario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scenario")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_id")
    private Long id;

    @Column(name = "scenario_type", nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    private String thumbnailUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected Scenario() {
    }

    public Scenario(Long id, String type, String title, String description, String thumbnailUrl, boolean active) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
