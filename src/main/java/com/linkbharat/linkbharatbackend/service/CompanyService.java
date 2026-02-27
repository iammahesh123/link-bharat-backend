package com.linkbharat.linkbharatbackend.service;

import com.linkbharat.linkbharatbackend.domain.model.CompanyRequest;
import com.linkbharat.linkbharatbackend.domain.model.CompanyResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request, String username);
    CompanyResponse updateCompany(Long companyId, CompanyRequest request, String username);
    CompanyResponse getCompanyById(Long companyId, String username);
    Page<CompanyResponse> getCompanies(String keyword, int page, int size);
    List<CompanyResponse> getMyCompanies(String username);
    void deleteCompany(Long companyId, String username);
    boolean toggleFollowCompany(Long companyId, String username);
}
