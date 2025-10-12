package com.linkbharat.linkbharatbackend.mapper;

import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    public UserProfileResponse toDTO(UserProfile userProfile, ModelMapper modelMapper) {
        return modelMapper.map(userProfile, UserProfileResponse.class);
    }
}
