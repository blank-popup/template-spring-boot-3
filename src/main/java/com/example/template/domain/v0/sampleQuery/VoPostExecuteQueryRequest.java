package com.example.template.domain.v0.sampleQuery;

import com.example.template.util.UtilJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@AllArgsConstructor
public class VoPostExecuteQueryRequest {
    private String query;

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
