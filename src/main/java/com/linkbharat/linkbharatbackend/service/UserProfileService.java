package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileRequest;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse createUserProfile(UserProfileRequest userProfileRequest);
    UserProfileResponse updateUserProfile(Long userProfileId, UserProfileRequest userProfileRequest);
    UserProfileResponse getUserProfileById(Long userProfileId);
    PaginationResponse<UserProfileResponse> getAllUserProfiles(PageModel pageModel);
    void deleteUserProfile(Long userProfileId);

    /** Get the current authenticated user's profile (looked up by their username) */
    UserProfileResponse getMyProfile(String username);

    /** Upsert (create or update) current user's profile from the request body */
    UserProfileResponse upsertMyProfile(String username, UserProfileRequest request);

    /** Get a profile by the AuthUser's primary key (the ID used in frontend URLs) */
    UserProfileResponse getProfileByAuthUserId(Long authUserId);
}
