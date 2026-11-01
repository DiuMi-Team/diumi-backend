package com.diumi.backend.visitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.visitor.dto.RegisterVisitorRequest;
import com.diumi.backend.visitor.dto.RegisterVisitorResponse;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.repository.AnonymousVisitorRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {
    @Mock AnonymousVisitorRepository visitorRepository;

    private VisitorService service;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-04T04:30:00Z"), TimeConfig.KST);
        service = new VisitorService(visitorRepository, clock);
        now = LocalDateTime.now(clock);
    }

    @Test
    void firstVisitIssuesNewVisitor() {
        when(visitorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterVisitorResponse response = service.register(new RegisterVisitorRequest(null));

        assertThat(response.isNew()).isTrue();
        assertThat(response.visitorId()).isNotNull();
        assertThat(response.createdAt().toString()).isEqualTo("2026-10-04T13:30+09:00");
    }

    @Test
    void emptyBodyIssuesNewVisitor() {
        when(visitorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterVisitorResponse response = service.register(null);

        assertThat(response.isNew()).isTrue();
    }

    @Test
    void existingVisitorUpdatesLastAccessedAt() {
        UUID visitorId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 30, 10, 0);
        AnonymousVisitor visitor = new AnonymousVisitor(visitorId, createdAt, createdAt);
        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));

        RegisterVisitorResponse response = service.register(new RegisterVisitorRequest(visitorId));

        assertThat(response.isNew()).isFalse();
        assertThat(response.visitorId()).isEqualTo(visitorId);
        assertThat(visitor.getLastAccessedAt()).isEqualTo(now);
        assertThat(visitor.getCreatedAt()).isEqualTo(createdAt);
        verify(visitorRepository, never()).save(any());
    }

    @Test
    void unknownVisitorIdIssuesNewVisitor() {
        UUID unknownId = UUID.randomUUID();
        when(visitorRepository.findById(unknownId)).thenReturn(Optional.empty());
        when(visitorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterVisitorResponse response = service.register(new RegisterVisitorRequest(unknownId));

        assertThat(response.isNew()).isTrue();
        assertThat(response.visitorId()).isNotEqualTo(unknownId);
    }
}
