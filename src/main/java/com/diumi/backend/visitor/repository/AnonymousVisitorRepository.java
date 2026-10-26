package com.diumi.backend.visitor.repository;

import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnonymousVisitorRepository extends JpaRepository<AnonymousVisitor, UUID> {
}
