package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoGetOneRequest {
    @NotNull
    @Positive(message = "ID must be greater 0")
    private Long id;

    public EntitySampleAha toEntity() {
        return EntitySampleAha
                .builder()
                .id(id)
                .build();
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
