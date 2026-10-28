package com.diumi.backend.action.repository;

import com.diumi.backend.action.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
