package com.linkbharat.linkbharatbackend.mapper;

import com.linkbharat.linkbharatbackend.domain.entity.Experience;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ExperienceMapper {
    public ExperienceResponse toDTO(Experience experience, ModelMapper modelMapper) {
        return modelMapper.map(experience, ExperienceResponse.class);
    }
}
