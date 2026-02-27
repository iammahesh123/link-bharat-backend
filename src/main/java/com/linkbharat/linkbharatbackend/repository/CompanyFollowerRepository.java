package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Company;
import com.linkbharat.linkbharatbackend.domain.entity.CompanyFollower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyFollowerRepository extends JpaRepository<CompanyFollower, Long> {
    Optional<CompanyFollower> findByCompanyAndUser(Company company, AuthUser user);
    boolean existsByCompanyAndUser(Company company, AuthUser user);
    Page<CompanyFollower> findByCompany(Company company, Pageable pageable);
    Page<CompanyFollower> findByUser(AuthUser user, Pageable pageable);
}
