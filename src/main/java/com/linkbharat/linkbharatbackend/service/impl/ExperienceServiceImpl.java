package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.Experience;
import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceRequest;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.repository.ExperienceRepository;
import com.linkbharat.linkbharatbackend.repository.UserProfileRepository;
import com.linkbharat.linkbharatbackend.service.ExperienceService;
import com.linkbharat.linkbharatbackend.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserProfileRepository userProfileRepository;
    private final Pagination pagination;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository,
                                 UserProfileRepository userProfileRepository,
                                 Pagination pagination) {
        this.experienceRepository = experienceRepository;
        this.userProfileRepository = userProfileRepository;
        this.pagination = pagination;
    }

    @Override
    public ExperienceResponse create(ExperienceRequest experienceRequest) {
        Experience experience = new Experience();
        BeanUtils.copyProperties(experienceRequest, experience, "userProfileId");
        if (experienceRequest.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(experienceRequest.getUserProfileId())
                    .orElseThrow(() -> new RuntimeException(
                            "UserProfile not found with id: " + experienceRequest.getUserProfileId()));
            experience.setUserProfile(userProfile);
        }
        Experience saved = experienceRepository.save(experience);
        return toResponseDTO(saved);
    }

    @Override
    public ExperienceResponse update(Long experienceId, ExperienceRequest experienceRequest) {
        Experience existing = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + experienceId));
        BeanUtils.copyProperties(experienceRequest, existing, "experienceId", "userProfileId");
        if (experienceRequest.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(experienceRequest.getUserProfileId())
                    .orElseThrow(() -> new RuntimeException(
                            "UserProfile not found with id: " + experienceRequest.getUserProfileId()));
            existing.setUserProfile(userProfile);
        }
        Experience saved = experienceRepository.save(existing);
        return toResponseDTO(saved);
    }

    @Override
    public ExperienceResponse getById(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + experienceId));
        return toResponseDTO(experience);
    }

    @Override
    public PaginationResponse<ExperienceResponse> getAll(PageModel pageModel) {
        Pageable pageable = pagination.applyPagination(pageModel);
        Page<Experience> experiencePage = experienceRepository.findAll(pageable);
        PaginationResponse<ExperienceResponse> response = new PaginationResponse<>();
        response.setContent(
                experiencePage.getContent().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toList())
        );
        response.setTotalPages(experiencePage.getTotalPages());
        response.setTotalRecords(experiencePage.getTotalElements());
        return response;
    }

    @Override
    public void delete(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + experienceId));
        experienceRepository.delete(experience);
    }

    private ExperienceResponse toResponseDTO(Experience experience) {
        ExperienceResponse response = new ExperienceResponse();
        BeanUtils.copyProperties(experience, response);
        return response;
    }
}
