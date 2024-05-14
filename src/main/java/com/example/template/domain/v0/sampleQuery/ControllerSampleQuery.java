package com.example.template.domain.v0.sampleQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RequestMapping("/api/v0/sample-query")
@RestController
@RequiredArgsConstructor
public class ControllerSampleQuery {
    private final ServiceSampleQuery serviceSampleQuery;

    @PostMapping("/execute-query")
    public ResponseEntity<?> executeQuery(@RequestBody VoPostExecuteQueryRequest postExecuteQueryRequest) throws RuntimeException {
        List<LinkedHashMap<String, Object>> result = serviceSampleQuery.executeQuery(postExecuteQueryRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
