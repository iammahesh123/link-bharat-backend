package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.ExperienceRequest;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;

public interface ExperienceService {
    ExperienceResponse create(ExperienceRequest experienceRequest);
    ExperienceResponse update(Long experienceId, ExperienceRequest experienceRequest);
    ExperienceResponse getById(Long experienceId);
    PaginationResponse<ExperienceResponse> getAll(PageModel pageModel);
    void delete(Long experienceId);
}
