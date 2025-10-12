package com.linkbharat.linkbharatbackend.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {
    private long totalPages;
    private long totalRecords;
    private List<T> content;
}
