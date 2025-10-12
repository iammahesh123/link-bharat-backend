package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.model.ExperienceRequest;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.service.ExperienceService;
import org.springframework.stereotype.Service;

@Service
public class ExperienceServiceImpl implements ExperienceService {
    @Override
    public ExperienceResponse create(ExperienceRequest experienceRequest) {
        return null;
    }

    @Override
    public ExperienceResponse update(Long experienceId, ExperienceRequest experienceRequest) {
        return null;
    }

    @Override
    public ExperienceResponse getById(Long experienceId) {
        return null;
    }

    @Override
    public PaginationResponse<ExperienceResponse> getAll(PageModel pageModel) {
        return null;
    }

    @Override
    public void delete(Long experienceId) {

    }
}
