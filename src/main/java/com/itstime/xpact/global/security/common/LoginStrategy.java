package com.itstime.xpact.global.security.common;

import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;

public interface LoginStrategy {

    Type supports();
}
