package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.global.security.common.LoginStrategy;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;

public class NaverLoginService implements LoginStrategy {


    @Override
    public Type supports() {
        return null;
    }

    public void login(String code) {
    }
}
