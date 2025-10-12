package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.EducationRequest;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;

public interface EducationService {
    EducationResponse createEducation(EducationRequest educationRequest);
    EducationResponse updateEducation(Long educationId, EducationRequest educationRequest);
    EducationResponse getEducationById(Long educationId);
    PaginationResponse<EducationResponse> getAllEducations(PageModel pageModel);
    void deleteEducation(Long educationId);
}
