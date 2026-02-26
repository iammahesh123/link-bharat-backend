package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.Education;
import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.repository.EducationRepository;
import com.linkbharat.linkbharatbackend.domain.model.EducationRequest;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.repository.UserProfileRepository;
import com.linkbharat.linkbharatbackend.service.EducationService;
import com.linkbharat.linkbharatbackend.utils.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserProfileRepository userProfileRepository;
    private final Pagination pagination;

    public EducationServiceImpl(EducationRepository educationRepository,
                                UserProfileRepository userProfileRepository,
                                Pagination pagination) {
        this.educationRepository = educationRepository;
        this.userProfileRepository = userProfileRepository;
        this.pagination = pagination;
    }

    @Override
    public EducationResponse createEducation(EducationRequest educationRequest) {
        System.out.println(educationRequest.toString());
        log.info("Creating Education {} for UserProfile ID: {}", educationRequest.getInstitutionName(), educationRequest.getUserProfileId());
        Education education = new Education();
        BeanUtils.copyProperties(educationRequest, education);
        education.setInstitutionName(educationRequest.getInstitutionName());
        education.setDescription(educationRequest.getDescription());
        education.setDegree(educationRequest.getDegree());
        education.setGrade(educationRequest.getGrade());
        education.setStartYear(educationRequest.getStartYear());
        education.setEndYear(educationRequest.getEndYear());
        if (educationRequest.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(educationRequest.getUserProfileId()).orElseThrow(
                    () -> new RuntimeException("UserProfile not found with id: " + educationRequest.getUserProfileId())
            );
            education.setUserProfile(userProfile);
        }
        Education savedEducation = educationRepository.save(education);
        return toResponseDTO(savedEducation);
    }

    @Override
    public EducationResponse updateEducation(Long educationId, EducationRequest educationRequest) {
        Education existingEducation = educationRepository.findById(educationId).orElseThrow(
                () -> new RuntimeException("Education not found with id: " + educationId)
        );
        BeanUtils.copyProperties(educationRequest, existingEducation, "userProfileId");
        if (educationRequest.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(educationRequest.getUserProfileId()).orElseThrow(
                    () -> new RuntimeException("UserProfile not found with id: " + educationRequest.getUserProfileId())
            );
            existingEducation.setUserProfile(userProfile);
        }
        Education savedEducation = educationRepository.save(existingEducation);
        return toResponseDTO(savedEducation);
    }

    @Override
    public EducationResponse getEducationById(Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(
                () -> new RuntimeException("Education not found with id: " + educationId)
        );
        return toResponseDTO(education);
    }

    @Override
    public PaginationResponse<EducationResponse> getAllEducations(PageModel pageModel) {
        Pageable pageable = pagination.applyPagination(pageModel);
        Page<Education> educationPage = educationRepository.findAll(pageable);
        PaginationResponse<EducationResponse> response = new PaginationResponse<>();
        response.setContent(
                educationPage.getContent().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toList())
        );
        response.setTotalPages(educationPage.getTotalPages());
        response.setTotalRecords(educationPage.getTotalElements());
        return response;
    }

    @Override
    public void deleteEducation(Long educationId) {
        Education education = educationRepository.findById(educationId).orElseThrow(
                () -> new RuntimeException("Education not found with id: " + educationId)
        );
        educationRepository.delete(education);
    }

    private EducationResponse toResponseDTO(Education education) {
        EducationResponse educationResponse = new EducationResponse();
        BeanUtils.copyProperties(education, educationResponse);
        return educationResponse;
    }
}
