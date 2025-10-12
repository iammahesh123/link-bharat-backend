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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/experience")
@Tag(
        name = "Experience Management",
        description = "Handles CRUD operations and pagination for user professional experience records."
)
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @Operation(
            summary = "Create a new experience entry",
            description = "Adds a new professional experience record for a user.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Experience details to be created",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "companyName": "Tech Bharat Pvt Ltd",
                              "position": "Software Engineer",
                              "startDate": "2021-06-01",
                              "endDate": "2023-03-15",
                              "description": "Worked on backend services using Spring Boot and Kafka integration",
                              "location": "Bengaluru, India"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Experience created successfully",
                            content = @Content(schema = @Schema(implementation = ExperienceResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ExperienceResponse> createExperience(@RequestBody ExperienceRequest experienceRequest) {
        return ResponseEntity.ok(experienceService.create(experienceRequest));
    }

    @Operation(
            summary = "Update an existing experience entry",
            description = "Updates details of an existing experience record identified by its ID.",
            parameters = {
                    @Parameter(name = "experience_id", description = "Unique ID of the experience record", example = "1001", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Updated experience information",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "companyName": "Tech Bharat Pvt Ltd",
                              "position": "Senior Software Engineer",
                              "startDate": "2021-06-01",
                              "endDate": "2024-01-15",
                              "description": "Led a backend development team working on microservices and Kafka",
                              "location": "Hyderabad, India"
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Experience updated successfully",
                            content = @Content(schema = @Schema(implementation = ExperienceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Experience not found", content = @Content)
            }
    )
    @PutMapping("/{experience_id}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable("experience_id") Long experienceId,
            @RequestBody ExperienceRequest experienceRequest) {
        return ResponseEntity.ok(experienceService.update(experienceId, experienceRequest));
    }

    @Operation(
            summary = "Get experience by ID",
            description = "Fetches the details of a specific experience record by its unique ID.",
            parameters = {
                    @Parameter(name = "experience_id", description = "Unique ID of the experience record", example = "1001", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Experience retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ExperienceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Experience not found", content = @Content)
            }
    )
    @GetMapping("/{experience_id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@PathVariable("experience_id") Long experienceId) {
        return ResponseEntity.ok(experienceService.getById(experienceId));
    }

    @Operation(
            summary = "Get paginated list of experiences",
            description = "Retrieves a paginated and optionally sorted list of all experiences.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
                    @Parameter(name = "size", description = "Number of records per page", example = "10"),
                    @Parameter(name = "sortBy", description = "Column to sort by", example = "startDate"),
                    @Parameter(name = "sortDir", description = "Sorting direction (ASC or DESC)", example = "ASC")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paginated experience list retrieved successfully",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<ExperienceResponse>> getAllExperiences(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") OrderBy sortDir) {
        PageModel pageModel = new PageModel(page, size, sortBy, sortDir);
        return ResponseEntity.ok(experienceService.getAll(pageModel));
    }

    @Operation(
            summary = "Delete experience by ID",
            description = "Deletes a specific experience record identified by its unique ID.",
            parameters = {
                    @Parameter(name = "experience_id", description = "Unique ID of the experience record", example = "1001", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Experience deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Experience not found", content = @Content)
            }
    )
    @DeleteMapping("/{experience_id}")
    public ResponseEntity<HttpStatus> deleteExperience(@PathVariable("experience_id") Long experienceId) {
        experienceService.delete(experienceId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
