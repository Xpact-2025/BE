package com.itstime.xpact.domain.member.dto.request;

import com.itstime.xpact.domain.recruit.dto.request.DesiredRecruitRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 프로필 등록 요청 DTO")
public record MemberSaveRequestDto(
        @Schema(description = "회원 이름",
                example = "홍길동")
        String name,

        @Schema(description = "사진 이미지 URL")
        String imgurl,

        @Schema(description = "회원 나이")
        Integer age,

        EducationSaveRequestDto educationSaveRequestDto,

        DesiredRecruitRequestDto desiredRecruitRequestDto
) {
}
