package com.example.template.domain.v0.sampleAha;

import com.example.template.util.UtilJson;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VoGetListResponse {
    private Long id;
    private String foo;
    private Long bar;

    @Builder
    private VoGetListResponse(long id, String foo, Long bar) {
        this.id = id;
        this.foo = foo;
        this.bar = bar;
    }

    public static VoGetListResponse of(EntitySampleAha entityResult) {
        return builder()
                .id(entityResult.getId())
                .foo(entityResult.getFoo())
                .bar(entityResult.getBar())
                .build();
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
