package com.example.template.domain.v0.sampleAha;

import com.example.template.setting.common.paging.Paging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RepositorySampleAha {
    Optional<EntitySampleAha> selectOne(EntitySampleAha entity);
    List<EntitySampleAha> selectList(Paging paging);
    List<EntitySampleAha> selectSearch(EntitySampleAha entity, Paging paging);

    int insertOne(EntitySampleAha entity);
    int insertList(List<EntitySampleAha> entities);

    int updateOne(EntitySampleAha entiry);
    int updateList(List<EntitySampleAha> entities);

    int deleteOne(EntitySampleAha entiry);
    int deleteList(List<EntitySampleAha> entities);
}
