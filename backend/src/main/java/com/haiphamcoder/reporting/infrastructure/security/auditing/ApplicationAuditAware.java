package com.haiphamcoder.reporting.infrastructure.security.auditing;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.haiphamcoder.reporting.domain.entity.User;

public class ApplicationAuditAware implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        } else {
            User userPrincipal = (User) authentication.getPrincipal();
            return Optional.ofNullable(userPrincipal.getId());
        }
    }

}
