package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoGetSearchRequest {
    @Size(max = 10)
    private String foo;
    @PositiveOrZero(message = "ID must be greater than or equal to 0")
    private Long bar;

    public EntitySampleAha toEntity() {
        return EntitySampleAha
                .builder()
                .foo(foo)
                .bar(bar)
                .build();
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
