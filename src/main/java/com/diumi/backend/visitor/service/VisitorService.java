package com.diumi.backend.visitor.service;

import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.visitor.dto.RegisterVisitorRequest;
import com.diumi.backend.visitor.dto.RegisterVisitorResponse;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.repository.AnonymousVisitorRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorService {
    private final AnonymousVisitorRepository visitorRepository;
    private final Clock clock;

    public VisitorService(AnonymousVisitorRepository visitorRepository, Clock clock) {
        this.visitorRepository = visitorRepository;
        this.clock = clock;
    }

    @Transactional
    public RegisterVisitorResponse register(RegisterVisitorRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        Optional<AnonymousVisitor> existing = findExisting(request);
        if (existing.isPresent()) {
            AnonymousVisitor visitor = existing.get();
            visitor.recordAccess(now);
            return toResponse(visitor, false);
        }

        AnonymousVisitor visitor = visitorRepository.save(new AnonymousVisitor(UUID.randomUUID(), now, now));
        return toResponse(visitor, true);
    }

    private Optional<AnonymousVisitor> findExisting(RegisterVisitorRequest request) {
        if (request == null || request.visitorId() == null) {
            return Optional.empty();
        }
        return visitorRepository.findById(request.visitorId());
    }

    private RegisterVisitorResponse toResponse(AnonymousVisitor visitor, boolean isNew) {
        return new RegisterVisitorResponse(visitor.getId(), isNew,
                toOffsetDateTime(visitor.getCreatedAt()), toOffsetDateTime(visitor.getLastAccessedAt()));
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(TimeConfig.KST).toOffsetDateTime();
    }
}
