package com.diumi.backend.visitor.web;

import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.repository.AnonymousVisitorRepository;
import java.util.UUID;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentVisitorArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String HEADER_NAME = "X-Visitor-Id";
    private final AnonymousVisitorRepository visitorRepository;

    public CurrentVisitorArgumentResolver(AnonymousVisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentVisitor.class)
                && parameter.getParameterType().equals(AnonymousVisitor.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String rawVisitorId = webRequest.getHeader(HEADER_NAME);
        if (rawVisitorId == null || rawVisitorId.isBlank()) {
            throw new BusinessException(ErrorCode.VISITOR_REQUIRED);
        }

        try {
            UUID visitorId = UUID.fromString(rawVisitorId);
            return visitorRepository.findById(visitorId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.VISITOR_REQUIRED));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.VISITOR_REQUIRED);
        }
    }
}
