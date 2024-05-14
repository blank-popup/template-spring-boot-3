package com.example.template.domain.v0.sampleAha;

import com.example.template.setting.common.paging.Paging;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceSampleAha {
    private final RepositorySampleAha repositorySampleAha;

    public VoGetOneResponse getOne(VoGetOneRequest getOneRequest) {
        EntitySampleAha entityInput = getOneRequest.toEntity();
        log.info(entityInput.toString());
        EntitySampleAha entityOutput = repositorySampleAha.selectOne(entityInput)
                .orElseThrow(() -> new ExceptionSampleAha("Cannot find such ID"));
        return VoGetOneResponse.of(entityOutput);
    }

    public List<VoGetListResponse> getList(Paging paging) {
        List<EntitySampleAha> entitiesOutput = repositorySampleAha.selectList(paging);
        return entitiesOutput
                .stream()
                .map(VoGetListResponse::of)
                .collect(Collectors.toList());
    }

    public List<VoGetSearchResponse> getSearch(VoGetSearchRequest getSearchRequest, Paging paging) {
        EntitySampleAha entityInput = getSearchRequest.toEntity();
        log.info(entityInput.toString());
        List<EntitySampleAha> entitiesOutput = repositorySampleAha.selectSearch(entityInput, paging);
        return entitiesOutput
                .stream()
                .map(VoGetSearchResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public VoAddOneResponse addOne(VoAddOneRequest postOneRequest) {
        EntitySampleAha entityInput = postOneRequest.toEntity();
        log.info(entityInput.toString());
        int countRow = repositorySampleAha.insertOne(entityInput);
        return VoAddOneResponse.of(countRow);
    }

    @Transactional
    public VoAddListResponse addList(List<VoAddListRequest> postListRequests) {
        List<EntitySampleAha> entitiesInput = postListRequests
                .stream()
                .map(VoAddListRequest::toEntity)
                .collect(Collectors.toList());
        log.info(entitiesInput.toString());
        int countRow = repositorySampleAha.insertList(entitiesInput);
        return VoAddListResponse.of(countRow);
    }

    @Transactional
    public VoModifyOneResponse modifyOne(VoModifyOneRequest putOneRequest) {
        EntitySampleAha entityInput = putOneRequest.toEntity();
        log.info(entityInput.toString());
        int countRow = repositorySampleAha.updateOne(entityInput);
        return VoModifyOneResponse.of(countRow);
    }

    @Transactional
    public VoModifyListResponse modifyList(List<VoModifyListRequest> putListRequests) {
        List<EntitySampleAha> entitiesInput = putListRequests
                .stream()
                .map(VoModifyListRequest::toEntity)
                .collect(Collectors.toList());
        log.info(entitiesInput.toString());
        int countRow = repositorySampleAha.updateList(entitiesInput);
        return VoModifyListResponse.of(countRow);
    }

    @Transactional
    public VoRemoveOneResponse removeOne(VoRemoveOneRequest deleteOneRequest) {
        EntitySampleAha entityInput = deleteOneRequest.toEntity();
        log.info(entityInput.toString());
        entityInput.delete();
        int countRow = repositorySampleAha.deleteOne(entityInput);
        return VoRemoveOneResponse.of(countRow);
    }

    @Transactional
    public VoRemoveListResponse removeList(List<VoRemoveListRequest> deleteListRequests) {
        List<EntitySampleAha> entitiesInput = deleteListRequests
                .stream()
                .map(VoRemoveListRequest::toEntity)
                .collect(Collectors.toList());
        log.info(entitiesInput.toString());
        entitiesInput.forEach(EntitySampleAha::delete);
        int countRow = repositorySampleAha.deleteList(entitiesInput);
        return VoRemoveListResponse.of(countRow);
    }
}
