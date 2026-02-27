package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Company;
import com.linkbharat.linkbharatbackend.domain.entity.Job;
import com.linkbharat.linkbharatbackend.domain.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByStatusAndIsDeletedFalseOrderByCreatedAtDesc(JobStatus status, Pageable pageable);

    Page<Job> findByCompanyAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(Company company, JobStatus status, Pageable pageable);

    Page<Job> findByPostedByAndIsDeletedFalseOrderByCreatedAtDesc(AuthUser postedBy, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND j.status = 'ACTIVE' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY j.createdAt DESC")
    Page<Job> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:employmentType IS NULL OR j.employmentType = :employmentType) " +
           "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
           "AND (:locationType IS NULL OR j.locationType = :locationType) " +
           "ORDER BY j.createdAt DESC")
    Page<Job> searchWithFilters(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("employmentType") EmploymentType employmentType,
            @Param("experienceLevel") ExperienceLevel experienceLevel,
            @Param("locationType") LocationType locationType,
            Pageable pageable);

    @Query("SELECT j FROM Job j JOIN j.skills s WHERE j.isDeleted = false AND j.status = 'ACTIVE' AND LOWER(s) IN :skills ORDER BY j.createdAt DESC")
    Page<Job> findBySkillsIn(@Param("skills") java.util.Set<String> skills, Pageable pageable);

    long countByCompanyAndIsDeletedFalse(Company company);
}
