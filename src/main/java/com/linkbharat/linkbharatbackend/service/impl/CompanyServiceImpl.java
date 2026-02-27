package com.linkbharat.linkbharatbackend.service.impl;

import com.linkbharat.linkbharatbackend.domain.entity.*;
import com.linkbharat.linkbharatbackend.domain.model.CompanyRequest;
import com.linkbharat.linkbharatbackend.domain.model.CompanyResponse;
import com.linkbharat.linkbharatbackend.domain.model.UserSummaryResponse;
import com.linkbharat.linkbharatbackend.repository.*;
import com.linkbharat.linkbharatbackend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyAdminRepository companyAdminRepository;
    private final CompanyFollowerRepository companyFollowerRepository;
    private final JobRepository jobRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request, String username) {
        AuthUser user = findUserByUsername(username);

        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setIndustry(request.getIndustry());
        company.setWebsite(request.getWebsite());
        company.setLogoUrl(request.getLogoUrl());
        company.setCoverImageUrl(request.getCoverImageUrl());
        company.setLocation(request.getLocation());
        company.setEmployeeCount(request.getEmployeeCount());
        company.setFoundedYear(request.getFoundedYear());
        company.setCreatedByUser(user);

        Company saved = companyRepository.save(company);

        // Add creator as OWNER
        CompanyAdmin admin = new CompanyAdmin();
        admin.setCompany(saved);
        admin.setUser(user);
        admin.setRole("OWNER");
        companyAdminRepository.save(admin);

        return mapToResponse(saved, username);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long companyId, CompanyRequest request, String username) {
        AuthUser user = findUserByUsername(username);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (!companyAdminRepository.existsByCompanyAndUser(company, user)) {
            throw new RuntimeException("You are not authorized to update this company");
        }

        if (request.getName() != null) company.setName(request.getName());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getLogoUrl() != null) company.setLogoUrl(request.getLogoUrl());
        if (request.getCoverImageUrl() != null) company.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getLocation() != null) company.setLocation(request.getLocation());
        if (request.getEmployeeCount() != null) company.setEmployeeCount(request.getEmployeeCount());
        if (request.getFoundedYear() != null) company.setFoundedYear(request.getFoundedYear());

        Company saved = companyRepository.save(company);
        return mapToResponse(saved, username);
    }

    @Override
    public CompanyResponse getCompanyById(Long companyId, String username) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapToResponse(company, username);
    }

    @Override
    public Page<CompanyResponse> getCompanies(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companies;
        if (keyword != null && !keyword.isBlank()) {
            companies = companyRepository.searchByKeyword(keyword, pageable);
        } else {
            companies = companyRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        return companies.map(c -> mapToResponse(c, null));
    }

    @Override
    public List<CompanyResponse> getMyCompanies(String username) {
        AuthUser user = findUserByUsername(username);
        List<CompanyAdmin> adminRoles = companyAdminRepository.findByUser(user);
        return adminRoles.stream()
                .map(ca -> mapToResponse(ca.getCompany(), username))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCompany(Long companyId, String username) {
        AuthUser user = findUserByUsername(username);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyAdmin admin = companyAdminRepository.findByCompanyAndUser(company, user)
                .orElseThrow(() -> new RuntimeException("You are not authorized"));
        if (!"OWNER".equals(admin.getRole())) {
            throw new RuntimeException("Only the owner can delete the company");
        }

        company.setDeleted(true);
        companyRepository.save(company);
    }

    @Override
    @Transactional
    public boolean toggleFollowCompany(Long companyId, String username) {
        AuthUser user = findUserByUsername(username);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        var existing = companyFollowerRepository.findByCompanyAndUser(company, user);
        if (existing.isPresent()) {
            companyFollowerRepository.delete(existing.get());
            company.setFollowerCount(Math.max(0, company.getFollowerCount() - 1));
            companyRepository.save(company);
            return false;
        } else {
            CompanyFollower follower = new CompanyFollower();
            follower.setCompany(company);
            follower.setUser(user);
            companyFollowerRepository.save(follower);
            company.setFollowerCount(company.getFollowerCount() + 1);
            companyRepository.save(company);
            return true;
        }
    }

    private CompanyResponse mapToResponse(Company company, String username) {
        boolean isFollowing = false;
        if (username != null) {
            try {
                AuthUser user = findUserByUsername(username);
                isFollowing = companyFollowerRepository.existsByCompanyAndUser(company, user);
            } catch (Exception ignored) {}
        }

        long jobCount = jobRepository.countByCompanyAndIsDeletedFalse(company);

        UserSummaryResponse createdBy = UserSummaryResponse.builder()
                .id(company.getCreatedByUser().getId())
                .username(company.getCreatedByUser().getUsername())
                .email(company.getCreatedByUser().getEmail())
                .build();

        return CompanyResponse.builder()
                .companyId(company.getCompanyId())
                .name(company.getName())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .logoUrl(company.getLogoUrl())
                .coverImageUrl(company.getCoverImageUrl())
                .location(company.getLocation())
                .employeeCount(company.getEmployeeCount())
                .foundedYear(company.getFoundedYear())
                .followerCount(company.getFollowerCount())
                .isVerified(company.isVerified())
                .isFollowing(isFollowing)
                .jobCount(jobCount)
                .createdBy(createdBy)
                .createdAt(company.getCreatedAt())
                .build();
    }

    private AuthUser findUserByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
