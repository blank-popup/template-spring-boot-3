package com.example.template.setting.common.paging;

import com.example.template.util.UtilJson;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Paging {
    @Positive(message = "Page Number is positive")
    private Long pageNumber;
    @Positive(message = "Page size is positive")
    private Long pageSize;

    public Long getOffset() {
        if (pageNumber == null || pageSize == null) {
            return null;
        }
        return (pageNumber - 1) * pageSize;
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
