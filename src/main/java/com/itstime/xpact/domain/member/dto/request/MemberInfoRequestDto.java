package com.itstime.xpact.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record MemberInfoRequestDto(
        @Schema(description = "회원 이름",
                example = "홍길동")
        String name,

        @Schema(description = "사진 이미지 URL")
        String imgurl,

        @Schema(description = "회원 나이")
        Integer age
) {
}
