package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.enums.OrderBy;
import com.linkbharat.linkbharatbackend.domain.model.EducationRequest;
import com.linkbharat.linkbharatbackend.domain.model.EducationResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.service.EducationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// ⚠️ DO NOT import io.swagger.v3.oas.annotations.parameters.RequestBody

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;   // ✅ this includes Spring RequestBody

@CrossOrigin
@RestController
@RequestMapping("/api/education")
@Tag(
        name = "Education Management",
        description = "APIs for managing user education records."
)
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @Operation(
            summary = "Create a new education record",
            description = "Adds a new education entry for a user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Education details",
                    content = @Content(schema = @Schema(implementation = EducationRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education created successfully",
                            content = @Content(schema = @Schema(implementation = EducationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(
            @org.springframework.web.bind.annotation.RequestBody EducationRequest educationRequest) {

        return ResponseEntity.ok(educationService.createEducation(educationRequest));
    }

    @Operation(
            summary = "Update an existing education record",
            description = "Updates education entry by ID."
    )
    @PutMapping("/{education_id}")
    public ResponseEntity<EducationResponse> updateEducation(
            @PathVariable("education_id") Long educationId,
            @org.springframework.web.bind.annotation.RequestBody EducationRequest educationRequest) {

        return ResponseEntity.ok(educationService.updateEducation(educationId, educationRequest));
    }

    @Operation(summary = "Get education details by ID")
    @GetMapping("/{education_id}")
    public ResponseEntity<EducationResponse> getEducationById(
            @PathVariable("education_id") Long educationId) {

        return ResponseEntity.ok(educationService.getEducationById(educationId));
    }

    @Operation(summary = "Get all education records (paginated)")
    @GetMapping
    public ResponseEntity<PaginationResponse<EducationResponse>> getAllEducations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") OrderBy sortDir) {

        PageModel pageModel = new PageModel(page, size, sortBy, sortDir);
        return ResponseEntity.ok(educationService.getAllEducations(pageModel));
    }

    @Operation(summary = "Delete an education record by ID")
    @DeleteMapping("/{education_id}")
    public ResponseEntity<HttpStatus> deleteEducation(
            @PathVariable("education_id") Long educationId) {

        educationService.deleteEducation(educationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}