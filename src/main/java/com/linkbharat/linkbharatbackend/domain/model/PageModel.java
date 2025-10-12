package com.linkbharat.linkbharatbackend.domain.model;

import com.linkbharat.linkbharatbackend.domain.enums.OrderBy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class PageModel {
    private Integer pageNumber;
    private Integer recordSize;
    private String sortColumn;
    private OrderBy orderBy;
}
