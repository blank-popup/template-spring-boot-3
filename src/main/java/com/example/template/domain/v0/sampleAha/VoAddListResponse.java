package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VoAddListResponse {
    private String result;

    @Builder
    private VoAddListResponse(String result) {
        this.result = result;
    }

    public static VoAddListResponse of(int countRow) {
        String result = countRow > 0 ? "success" : "failure";
        return builder()
                .result(result)
                .build();
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
