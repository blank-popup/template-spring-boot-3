package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoGetListRequest {

    public EntitySampleAha toEntity() {
        return EntitySampleAha
                .builder()
                .build();
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
