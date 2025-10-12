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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/education")
@Tag(
        name = "Education Management",
        description = "APIs for managing user education records such as degree, institution, start and end dates."
)
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @Operation(
            summary = "Create a new education record",
            description = "Adds a new education entry for a user. The request must contain valid institution, degree, and date details.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Education details to create a new record",
                    content = @Content(schema = @Schema(implementation = EducationRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education created successfully",
                            content = @Content(schema = @Schema(implementation = EducationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(@RequestBody EducationRequest educationRequest) {
        return ResponseEntity.ok(educationService.createEducation(educationRequest));
    }

    @Operation(
            summary = "Update an existing education record",
            description = "Updates the details of an existing education entry by its ID.",
            parameters = {
                    @Parameter(name = "education_id", description = "Unique ID of the education record", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Updated education data",
                    content = @Content(schema = @Schema(implementation = EducationRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education updated successfully",
                            content = @Content(schema = @Schema(implementation = EducationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Education record not found", content = @Content)
            }
    )
    @PutMapping("/{education_id}")
    public ResponseEntity<EducationResponse> updateEducation(
            @PathVariable("education_id") Long educationId,
            @RequestBody EducationRequest educationRequest) {
        return ResponseEntity.ok(educationService.updateEducation(educationId, educationRequest));
    }

    @Operation(
            summary = "Get education details by ID",
            description = "Fetches a single education record using its unique ID.",
            parameters = {
                    @Parameter(name = "education_id", description = "Unique ID of the education record", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education record retrieved successfully",
                            content = @Content(schema = @Schema(implementation = EducationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Education record not found", content = @Content)
            }
    )
    @GetMapping("/{education_id}")
    public ResponseEntity<EducationResponse> getEducationById(@PathVariable("education_id") Long educationId) {
        return ResponseEntity.ok(educationService.getEducationById(educationId));
    }

    @Operation(
            summary = "Get all education records (paginated)",
            description = "Retrieves all education entries in paginated format with optional sorting.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (default: 0)", example = "0"),
                    @Parameter(name = "size", description = "Page size (default: 10)", example = "10"),
                    @Parameter(name = "sortBy", description = "Field name to sort by", example = "id"),
                    @Parameter(name = "sortDir", description = "Sort direction (asc or desc)", example = "asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of education records retrieved successfully",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<PaginationResponse<EducationResponse>> getAllEducations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") OrderBy sortDir) {

        PageModel pageModel = new PageModel(page, size, sortBy, sortDir);
        return ResponseEntity.ok(educationService.getAllEducations(pageModel));
    }

    @Operation(
            summary = "Delete an education record by ID",
            description = "Deletes an existing education entry permanently.",
            parameters = {
                    @Parameter(name = "education_id", description = "Unique ID of the education record", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Education record not found", content = @Content)
            }
    )
    @DeleteMapping("/{education_id}")
    public ResponseEntity<HttpStatus> deleteEducation(@PathVariable("education_id") Long educationId) {
        educationService.deleteEducation(educationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
