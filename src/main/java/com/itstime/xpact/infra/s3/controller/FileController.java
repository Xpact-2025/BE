package com.itstime.xpact.infra.s3.controller;

import com.itstime.xpact.global.response.RestResponse;
import com.itstime.xpact.infra.s3.dto.FileResponseDto;
import com.itstime.xpact.infra.s3.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
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

    @Operation(summary = "이미지 업로드 PresignedURL 얻기 API", description = """
            이미지 업로드 허용이 가능한 제한 시간이 설정된 PreSignedURL을 반환합니다.<br>
            이 URL에 PUT을 통해 업로드할 이미지를 업로드해주세요.<br>
            만약 비어있다면 기본 프로필로 이어집니다.<br>
            회원정보 저장 DTO 중 imgUrl에는 "fileUrl" 값을 저장해주세요.
            """)
    @GetMapping("/uploads/image")
    public ResponseEntity<RestResponse<FileResponseDto>> uploadImage(
            @Nullable String fileName
    ) {
        return ResponseEntity.ok(RestResponse.ok(fileService.getPreSignedProfileUrl(fileName)));
    }

    @Operation(summary = "조회 이미지 PresignedURL 얻기 API", description = """
            이미지를 조회할 URL을 반환합니다.<br>
            반환된 URL로 이미지에 접근이 가능합니다.<br>
            """)
    @GetMapping("/downloads/image")
    public ResponseEntity<RestResponse<String>> downloadImage() {
        return ResponseEntity.ok(
                RestResponse.ok(fileService.getProfileUrl())
        );
    }
}
