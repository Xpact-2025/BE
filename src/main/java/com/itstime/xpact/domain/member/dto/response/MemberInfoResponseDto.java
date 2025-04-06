package com.itstime.xpact.domain.member.dto.response;

import lombok.Builder;

@Builder
public record MemberInfoResponseDto (
        String name,
        String imgurl,
        int age,
        String education,
        String recruit
) {
}
