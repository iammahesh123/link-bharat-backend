package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;

import java.util.List;

public interface SearchService {
    List<UserSummaryResponse> searchUsers(String query, int page, int size, String currentUsername);
}
