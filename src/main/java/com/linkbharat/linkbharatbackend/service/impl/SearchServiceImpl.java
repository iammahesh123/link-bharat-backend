package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import com.linkbharat.linkbharatbackend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final AuthUserRepository authUserRepository;

    @Override
    public List<UserSummaryResponse> searchUsers(String query, int page, int size, String currentUsername) {
        AuthUser currentUser = authUserRepository.findByUsername(currentUsername)
                .or(() -> authUserRepository.findByEmail(currentUsername))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        PageRequest pageRequest = PageRequest.of(page, size);
        String trimmedQuery = query == null ? "" : query.trim().toLowerCase();

        return authUserRepository.searchByUsernameOrEmail(trimmedQuery, pageRequest)
                .getContent()
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }

    private UserSummaryResponse toUserSummary(AuthUser user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
