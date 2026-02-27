package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.isDeleted = false AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.industry) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Company> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Company> findByCreatedByUserAndIsDeletedFalse(AuthUser user);

    Page<Company> findByIndustryAndIsDeletedFalse(String industry, Pageable pageable);
}
