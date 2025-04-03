package com.itstime.xpact.global.security.dto.response;

import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import lombok.Builder;

@Builder
public record LoginResponseDto (
        String accessToken,
        String email,
        String name,
        Role role,
        Type type
){
}
