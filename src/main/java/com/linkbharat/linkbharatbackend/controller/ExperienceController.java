package com.linkbharat.linkbharatbackend.controller;

import com.linkbharat.linkbharatbackend.domain.enums.OrderBy;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceRequest;
import com.linkbharat.linkbharatbackend.domain.model.ExperienceResponse;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import com.linkbharat.linkbharatbackend.domain.model.PaginationResponse;
import com.linkbharat.linkbharatbackend.service.ExperienceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
// ❌ DO NOT import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;   // ✅ Spring RequestBody comes from here

@CrossOrigin
@RestController
@RequestMapping("/api/experience")
@Tag(
        name = "Experience Management",
        description = "Handles CRUD operations for user professional experience."
)
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @Operation(
            summary = "Create a new experience entry",
            description = "Adds a new professional experience record.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Experience payload",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "companyName": "Tech Bharat Pvt Ltd",
                              "position": "Software Engineer",
                              "startDate": "2021-06-01",
                              "endDate": "2023-03-15",
                              "description": "Worked on backend services",
                              "location": "Bengaluru, India"
                            }
                            """)
                    )
            )
    )
    @PostMapping
    public ResponseEntity<ExperienceResponse> createExperience(
            @org.springframework.web.bind.annotation.RequestBody ExperienceRequest experienceRequest) {

        return ResponseEntity.ok(experienceService.create(experienceRequest));
    }

    @Operation(summary = "Update an existing experience entry")
    @PutMapping("/{experience_id}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable("experience_id") Long experienceId,
            @org.springframework.web.bind.annotation.RequestBody ExperienceRequest experienceRequest) {

        return ResponseEntity.ok(experienceService.update(experienceId, experienceRequest));
    }

    @Operation(summary = "Get experience by ID")
    @GetMapping("/{experience_id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(
            @PathVariable("experience_id") Long experienceId) {

        return ResponseEntity.ok(experienceService.getById(experienceId));
    }

    @Operation(summary = "Get paginated list of experiences")
    @GetMapping
    public ResponseEntity<PaginationResponse<ExperienceResponse>> getAllExperiences(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") OrderBy sortDir) {

        PageModel pageModel = new PageModel(page, size, sortBy, sortDir);
        return ResponseEntity.ok(experienceService.getAll(pageModel));
    }

    @Operation(summary = "Delete experience by ID")
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<HttpStatus> deleteExperience(
            @PathVariable("experience_id") Long experienceId) {

        experienceService.delete(experienceId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}