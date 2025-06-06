package com.itstime.xpact.infra.s3.controller;

import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.infra.s3.dto.FileResponseDto;
import com.itstime.xpact.infra.s3.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/file")
public class FileController {

    private final FileService fileService;

    @GetMapping("/uploads")
    public ResponseEntity<RestResponse<FileResponseDto>> upload(String fileName) {
        return ResponseEntity.ok(RestResponse.ok(fileService.getPreSignedUrl(fileName)));
    }

    // key값으로 해당 파일의 s3 presigned 주소 반환
    @GetMapping("/downloads")
    public ResponseEntity<RestResponse<String>> download(String key) {
        return ResponseEntity.ok(RestResponse.ok(fileService.getFileUrl(key)));
    }
}
