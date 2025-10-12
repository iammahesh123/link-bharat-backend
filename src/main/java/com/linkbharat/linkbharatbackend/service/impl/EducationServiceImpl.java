package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.model.EducationRequest;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.service.EducationService;
import org.springframework.stereotype.Service;

@Service
public class EducationServiceImpl implements EducationService {
    @Override
    public EducationResponse createEducation(EducationRequest educationRequest) {
        return null;
    }

    @Override
    public EducationResponse updateEducation(Long educationId, EducationRequest educationRequest) {
        return null;
    }

    @Override
    public EducationResponse getEducationById(Long educationId) {
        return null;
    }

    @Override
    public PaginationResponse<EducationResponse> getAllEducations(PageModel pageModel) {
        return null;
    }

    @Override
    public void deleteEducation(Long educationId) {

    }
}
