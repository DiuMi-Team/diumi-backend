package com.diumi.backend.session.entity;

public enum SessionType {
    PRACTICE_MISSION,
    REAL_MISSION,
    REAL_FREE;

    public boolean requiresMission() {
        return this != REAL_FREE;
    }

    public boolean isPractice() {
        return this == PRACTICE_MISSION;
    }
}
