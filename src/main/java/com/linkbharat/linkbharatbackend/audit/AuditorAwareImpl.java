package com.linkbharat.linkbharatbackend.audit;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of(String.valueOf(authUser.getUsername()));
    }
}
