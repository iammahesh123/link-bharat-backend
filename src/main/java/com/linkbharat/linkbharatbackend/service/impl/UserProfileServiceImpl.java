package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileRequest;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;
import com.linkbharat.linkbharatbackend.exceptions.ResourceNotFoundException;
import com.linkbharat.linkbharatbackend.mapper.UserProfileMapper;
import com.linkbharat.linkbharatbackend.repository.UserProfileRepository;
import com.linkbharat.linkbharatbackend.service.UserProfileService;
import com.linkbharat.linkbharatbackend.utils.Pagination;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final ModelMapper modelMapper;
    private final Pagination pagination;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, ModelMapper modelMapper, Pagination pagination) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.modelMapper = modelMapper;
        this.pagination = pagination;
    }

    @Override
    public UserProfileResponse createUserProfile(UserProfileRequest userProfileRequest) {
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userProfileRequest, userProfile,"experience","education");
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toDTO(savedUserProfile,modelMapper);
    }

    @Override
    public UserProfileResponse updateUserProfile(Long userProfileId, UserProfileRequest userProfileRequest) {
        UserProfile existingUserProfile = userProfileRepository.findById(userProfileId).orElseThrow(
                () -> new ResourceNotFoundException("UserProfile not found with id: " + userProfileId)
        );
        BeanUtils.copyProperties(userProfileRequest, existingUserProfile,"experience","education");
        UserProfile updatedUserProfile = userProfileRepository.save(existingUserProfile);
        return userProfileMapper.toDTO(updatedUserProfile,modelMapper);
    }

    @Override
    public UserProfileResponse getUserProfileById(Long userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(
                () -> new ResourceNotFoundException("UserProfile not found with id: " + userProfileId)
        );
        return userProfileMapper.toDTO(userProfile,modelMapper);
    }

    @Override
    public PaginationResponse<UserProfileResponse> getAllUserProfiles(PageModel pageModel) {
        Pageable pageable = pagination.applyPagination(pageModel);
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        PaginationResponse<UserProfileResponse> response = new PaginationResponse<>();
        response.setContent(userProfilePage.stream().map(profile -> userProfileMapper.toDTO(profile, modelMapper)).collect(Collectors.toList()));
        response.setTotalPages(userProfilePage.getNumber());
        response.setTotalRecords(userProfilePage.getSize());
        return response;
    }

    @Override
    public void deleteUserProfile(Long userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(
                () -> new ResourceNotFoundException("UserProfile not found with id: " + userProfileId)
        );
        userProfile.setDeleted(true);
        userProfileRepository.save(userProfile);
    }
}
