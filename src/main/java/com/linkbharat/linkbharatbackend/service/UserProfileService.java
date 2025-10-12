package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileRequest;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse createUserProfile(UserProfileRequest userProfileRequest);
    UserProfileResponse updateUserProfile(Long userProfileId,UserProfileRequest userProfileRequest);
    UserProfileResponse getUserProfileById(Long userProfileId);
    PaginationResponse<UserProfileResponse> getAllUserProfiles(PageModel pageModel);
    void deleteUserProfile(Long userProfileId);
}
