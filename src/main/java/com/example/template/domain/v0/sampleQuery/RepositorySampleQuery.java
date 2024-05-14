package com.example.template.domain.v0.sampleQuery;

import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface RepositorySampleQuery {
    List<LinkedHashMap<String, Object>> executeQuery(VoPostExecuteQueryRequest postExecuteQueryRequest);
}
