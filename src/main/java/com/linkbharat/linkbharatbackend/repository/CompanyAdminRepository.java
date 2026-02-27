package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.Company;
import com.linkbharat.linkbharatbackend.domain.entity.CompanyAdmin;
import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAdminRepository extends JpaRepository<CompanyAdmin, Long> {
    List<CompanyAdmin> findByCompany(Company company);
    List<CompanyAdmin> findByUser(AuthUser user);
    Optional<CompanyAdmin> findByCompanyAndUser(Company company, AuthUser user);
    boolean existsByCompanyAndUser(Company company, AuthUser user);
}
