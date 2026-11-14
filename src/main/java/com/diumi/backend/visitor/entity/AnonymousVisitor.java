package com.diumi.backend.visitor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "anonymous_visitor")
public class AnonymousVisitor {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "visitor_id", columnDefinition = "varchar(36)")
    private UUID id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    protected AnonymousVisitor() {
    }

    public AnonymousVisitor(UUID id, LocalDateTime createdAt, LocalDateTime lastAccessedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
    }

    public UUID getId() {
        return id;
    }
}
