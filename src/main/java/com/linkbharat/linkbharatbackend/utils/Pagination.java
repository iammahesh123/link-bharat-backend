package com.linkbharat.linkbharatbackend.utils;

import com.linkbharat.linkbharatbackend.domain.enums.OrderBy;
import com.linkbharat.linkbharatbackend.domain.model.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Pagination {
    public  Pageable applyPagination(final PageModel pageModel) {
        if (Objects.isNull(pageModel.getRecordSize()) || pageModel.getRecordSize() == 0) {
            pageModel.setRecordSize(2);
        }
        if (Objects.isNull(pageModel.getPageNumber())) {
            pageModel.setPageNumber(0);
        }
        Pageable pageable = null;
        if (StringUtils.isBlank(pageModel.getSortColumn())) {
            pageable = PageRequest.of(pageModel.getPageNumber(), pageModel.getRecordSize(),
                    Sort.by("createdAt").descending());
        } else if (pageModel.getOrderBy() == OrderBy.DESC) {
            pageable = PageRequest.of(pageModel.getPageNumber(), pageModel.getRecordSize(),
                    Sort.by(pageModel.getSortColumn()).descending());
        } else if (pageModel.getOrderBy() == OrderBy.ASC) {
            pageable = PageRequest.of(pageModel.getPageNumber(), pageModel.getRecordSize(),
                    Sort.by(pageModel.getSortColumn()));
        } else {
            pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        }
        return pageable;
    }
}
