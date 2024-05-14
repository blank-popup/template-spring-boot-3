package com.example.template.domain.v0.sampleAha;

import com.example.template.setting.common.paging.Paging;
import com.example.template.setting.exception.CodeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RequestMapping("/api/v0/sample-aha")
@RestController
@RequiredArgsConstructor
public class ControllerSampleAha {
    private final ServiceSampleAha serviceSampleAha;

    @GetMapping("/one")
    public ResponseEntity<?> getOne(@Valid VoGetOneRequest getOneRequest, BindingResult bindingResult) {
        log.info("METHOD GET called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        log.info(getOneRequest.toString());
        VoGetOneResponse getOneResponse = serviceSampleAha.getOne(getOneRequest);
        log.info(getOneResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getOneResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getList(@Valid Paging paging, BindingResult bindingResult) {
        log.info("METHOD GET called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        List<VoGetListResponse> getListResponse = serviceSampleAha.getList(paging);
        log.info(getListResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getListResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getSearch(@Valid VoGetSearchRequest getSearchRequest, BindingResult bindingResultRequest, @Valid Paging paging, BindingResult bindingResultPaging) {
        log.info("METHOD GET called");
        if (bindingResultRequest.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter Request");
        }
        if (bindingResultPaging.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter Paging");
        }
        if ("ERROR".equals(getSearchRequest.getFoo())) {
            throw new ExceptionSampleAha("Exception occur", CodeException.INTERNAL_SERVER_ERROR);
        }
        log.info(getSearchRequest.toString());
        List<VoGetSearchResponse> getSearchResponse = serviceSampleAha.getSearch(getSearchRequest, paging);
        log.info(getSearchResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getSearchResponse);
    }

    @PostMapping("/one")
    public ResponseEntity<?> addOne(@RequestBody @Valid VoAddOneRequest addOneRequest, BindingResult bindingResult) {
        log.info("METHOD POST called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        if ("ERROR".equals(addOneRequest.getFoo())) {
            throw new ExceptionSampleAha("Exception occur", CodeException.INTERNAL_SERVER_ERROR);
        }
        log.info(addOneRequest.toString());
        VoAddOneResponse addOneResponse = serviceSampleAha.addOne(addOneRequest);
        log.info(addOneResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addOneResponse);
    }

    @PostMapping("/list")
    public ResponseEntity<?> addList(@RequestBody @Valid List<VoAddListRequest> addListRequest, BindingResult bindingResult) {
        log.info("METHOD POST called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        if ("ERROR".equals(addListRequest.get(0).getFoo())) {
            throw new ExceptionSampleAha("Exception occur", CodeException.INTERNAL_SERVER_ERROR);
        }
        log.info(addListRequest.toString());
        VoAddListResponse addListResponse = serviceSampleAha.addList(addListRequest);
        log.info(addListResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addListResponse);
    }

    @PutMapping("/one")
    public ResponseEntity<?> modifyOne(@RequestBody @Valid VoModifyOneRequest modifyOneRequest, BindingResult bindingResult) {
        log.info("METHOD PUT called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        if ("ERROR".equals(modifyOneRequest.getFoo())) {
            throw new ExceptionSampleAha("Exception occur", CodeException.INTERNAL_SERVER_ERROR);
        }
        log.info(modifyOneRequest.toString());
        VoModifyOneResponse modifyOneResponse = serviceSampleAha.modifyOne(modifyOneRequest);
        log.info(modifyOneResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(modifyOneResponse);
    }

    @PutMapping("/list")
    public ResponseEntity<?> modifyList(@RequestBody @Valid List<VoModifyListRequest> modifyListRequest, BindingResult bindingResult) {
        log.info("METHOD PUT called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        if ("ERROR".equals(modifyListRequest.get(0).getFoo())) {
            throw new ExceptionSampleAha("Exception occur", CodeException.INTERNAL_SERVER_ERROR);
        }
        log.info(modifyListRequest.toString());
        VoModifyListResponse modifyListResponse = serviceSampleAha.modifyList(modifyListRequest);
        log.info(modifyListResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(modifyListResponse);
    }

    @DeleteMapping("/one")
    public ResponseEntity<?> removeOne(@RequestBody @Valid VoRemoveOneRequest removeOneRequest, BindingResult bindingResult) {
        log.info("METHOD DELETE called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        log.info(removeOneRequest.toString());
        VoRemoveOneResponse removeOneResponse = serviceSampleAha.removeOne(removeOneRequest);
        log.info(removeOneResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removeOneResponse);
    }

    @DeleteMapping("/list")
    public ResponseEntity<?> removeList(@RequestBody @Valid List<VoRemoveListRequest> removeListRequests, BindingResult bindingResult) {
        log.info("METHOD DELETE called");
        if (bindingResult.hasErrors()) {
            throw new ExceptionSampleAha("Invalid Parameter");
        }
        log.info(removeListRequests.toString());
        VoRemoveListResponse removeListResponse = serviceSampleAha.removeList(removeListRequests);
        log.info(removeListResponse.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removeListResponse);
    }

    private String dirBase = "D:\\WorkSpace\\files_template\\";

    @GetMapping(value="/file/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable(value="filename") String filename) throws IOException {
        File fileDownload = new File(dirBase + filename);
        StreamingResponseBody streamingResponseBody = outputStream -> {
            Files.copy(fileDownload.toPath(), outputStream);
        };
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileDownload.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileDownload.length())
                .body(streamingResponseBody);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        File fileUpload = new File(dirBase + originalFilename);
        file.transferTo(fileUpload);

        return ResponseEntity
                .ok()
                .build();
    }
}
