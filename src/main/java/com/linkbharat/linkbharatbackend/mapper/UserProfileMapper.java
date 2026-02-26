package com.linkbharat.linkbharatbackend.mapper;

import com.linkbharat.linkbharatbackend.domain.entity.Education;
import com.linkbharat.linkbharatbackend.domain.entity.Experience;
import com.linkbharat.linkbharatbackend.domain.entity.UserProfile;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserProfileResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserProfileMapper {

    public UserProfileResponse toDTO(UserProfile userProfile, ModelMapper modelMapper) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserProfileId(userProfile.getUserProfileId());
        response.setName(userProfile.getName());
        response.setHeadLine(userProfile.getHeadLine());
        response.setAvatarUrl(userProfile.getAvatarUrl());
        response.setCoverageImageUrl(userProfile.getCoverageImageUrl());
        response.setLocation(userProfile.getLocation());
        response.setConnections(userProfile.getConnections());
        response.setAbout(userProfile.getAbout());
        response.setSkills(userProfile.getSkills());
        response.setConnected(userProfile.isConnected());
        response.setPending(userProfile.isPending());
        response.setOnboardingComplete(userProfile.isOnboardingComplete());

        // Set the AuthUser ID so frontend can build profile URLs (/profile/{id})
        if (userProfile.getAuthUser() != null) {
            response.setId(userProfile.getAuthUser().getId());
        }

        // Map experiences
        if (userProfile.getExperiences() != null) {
            List<ExperienceResponse> experienceResponses = new ArrayList<>();
            for (Experience exp : userProfile.getExperiences()) {
                ExperienceResponse er = new ExperienceResponse();
                er.setExperienceId(exp.getExperienceId());
                er.setTitle(exp.getTitle());
                er.setEmploymentType(exp.getEmploymentType());
                er.setCompanyName(exp.getCompanyName());
                er.setStartDate(exp.getStartDate());
                er.setEndDate(exp.getEndDate());
                er.setLocation(exp.getLocation());
                er.setLocationType(exp.getLocationType());
                er.setDescription(exp.getDescription());
                er.setTop5Skills(exp.getTop5Skills());
                er.setMediaUrl(exp.getMediaUrl());
                er.setCurrentJob(exp.isCurrentJob());
                experienceResponses.add(er);
            }
            response.setExperiences(experienceResponses);
        }

        // Map educations
        if (userProfile.getEducations() != null) {
            List<EducationResponse> educationResponses = new ArrayList<>();
            for (Education edu : userProfile.getEducations()) {
                EducationResponse er = new EducationResponse();
                er.setEducationId(edu.getEducationId());
                er.setInstitutionName(edu.getInstitutionName());
                er.setDegree(edu.getDegree());
                er.setFieldOfStudy(edu.getFieldOfStudy());
                er.setStartYear(edu.getStartYear());
                er.setEndYear(edu.getEndYear());
                er.setGrade(edu.getGrade());
                er.setDescription(edu.getDescription());
                educationResponses.add(er);
            }
            response.setEducations(educationResponses);
        }

        return response;
    }
}
