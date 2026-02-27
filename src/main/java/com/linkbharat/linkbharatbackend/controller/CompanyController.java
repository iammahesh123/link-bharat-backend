package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.model.CompanyRequest;
import com.linkbharat.linkbharatbackend.domain.model.CompanyResponse;
import com.linkbharat.linkbharatbackend.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "APIs for managing company pages, following companies, and viewing company details.")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Create a new company page")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request, userDetails.getUsername()));
    }

    @Operation(summary = "Get paginated list of companies with optional search")
    @GetMapping
    public ResponseEntity<Page<CompanyResponse>> getCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(companyService.getCompanies(keyword, page, size));
    }

    @Operation(summary = "Get company by ID")
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(companyService.getCompanyById(companyId, userDetails.getUsername()));
    }

    @Operation(summary = "Update a company page")
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(companyService.updateCompany(companyId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Delete a company (soft delete, owner only)")
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        companyService.deleteCompany(companyId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Toggle follow/unfollow a company")
    @PostMapping("/{companyId}/follow")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isFollowing = companyService.toggleFollowCompany(companyId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("following", isFollowing));
    }

    @Operation(summary = "Get companies managed by the current user")
    @GetMapping("/my")
    public ResponseEntity<List<CompanyResponse>> getMyCompanies(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(companyService.getMyCompanies(userDetails.getUsername()));
    }
}
