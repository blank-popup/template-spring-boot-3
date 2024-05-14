package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VoModifyOneResponse {
    private String result;

    @Builder
    private VoModifyOneResponse(String result) {
        this.result = result;
    }

    public static VoModifyOneResponse of(int countRow) {
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
