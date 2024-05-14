package com.example.template.domain.v0.sampleQuery;

import com.example.template.util.UtilJson;
import lombok.Getter;

@Getter
public class VoPostExecuteQueryResponse {
    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
