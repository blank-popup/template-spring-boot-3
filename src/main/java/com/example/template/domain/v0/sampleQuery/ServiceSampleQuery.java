package com.example.template.domain.v0.sampleQuery;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceSampleQuery {
    private final RepositorySampleQuery repositorySampleQuery;

    public List<LinkedHashMap<String, Object>> executeQuery(VoPostExecuteQueryRequest postExecuteQueryRequest) {
        List<LinkedHashMap<String, Object>> result = repositorySampleQuery.executeQuery(postExecuteQueryRequest);
        return result;
    }
}
