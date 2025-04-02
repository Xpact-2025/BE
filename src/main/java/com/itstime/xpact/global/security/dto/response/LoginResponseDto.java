package com.itstime.xpact.global.security.dto.response;

import com.itstime.xpact.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record LoginResponseDto (
        String accessToken
){
}
