package com.linkbharat.linkbharatbackend.mapper;

import com.linkbharat.linkbharatbackend.domain.entity.Education;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EducationMapper {
    public EducationResponse toDTO(Education education, ModelMapper modelMapper) {
        return modelMapper.map(education, EducationResponse.class);
    }
}
