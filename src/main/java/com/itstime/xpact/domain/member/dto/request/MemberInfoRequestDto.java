package com.itstime.xpact.domain.member.dto.request;

import com.itstime.xpact.domain.recruit.entity.Recruit;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record MemberInfoRequestDto(
        @Schema(description = "회원 이름",
                example = "홍길동")
        String name,

        @Schema(description = "사진 이미지 URL")
        String imgurl,

        @Schema(description = "회원 나이")
        Integer age,

        @Schema(description = "회원 학력 이름, Education에서 입력한 학력 정보의 String을 사용합니다.",
                example = "잇타대학교 경영학과 재학")
        String schoolInfo,

        @Schema(description = "희망 직무",
                example = "회계사")
        Recruit recruit
) {
}
