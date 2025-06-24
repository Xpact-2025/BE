package com.itstime.xpact.infra.s3.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponseDto {
    private String preSignedUrl;
    private String fileUrl;

    public static FileResponseDto of(String preSignedUrl, String fileUrl) {
        return FileResponseDto.builder().preSignedUrl(preSignedUrl).fileUrl(fileUrl).build();
    }
}
