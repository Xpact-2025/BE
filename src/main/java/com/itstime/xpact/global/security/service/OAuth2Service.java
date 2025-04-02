package com.itstime.xpact.global.security.service;

import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.security.dto.request.LoginRequestDto;
import com.itstime.xpact.global.security.dto.request.SignupRequestDto;
import com.itstime.xpact.global.security.dto.response.LoginResponseDto;
import com.itstime.xpact.global.security.dto.response.SignupResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final FormLoginService formLoginService;
    private final KakaoLoginService kakaoLoginService;
//    private final NaverLoginService naverLoginService;

//    public LoginResponseDto loginWithCode(String code) throws CustomException {
//        Type type = requestDto.type();
//        try {
//            if (type.equals(kakaoLoginService.supports())) {
//                kakaoLoginService.login(code);
//            } else if (type.equals(naverLoginService.supports())) {
//                naverLoginService.login(code);
//            }
//        } catch (CustomException e) {
//            throw CustomException.of(ErrorCode.)
//        }
//    }

    @Transactional
    public LoginResponseDto formLogin(
            LoginRequestDto requestDto,
            HttpServletResponse httpResponse
    ) throws CustomException {
        return formLoginService.generalLogin(requestDto, httpResponse);
    }

    @Transactional
    public SignupResponseDto register(SignupRequestDto request) throws CustomException {
        return formLoginService.register(request);
    }

    @Transactional
    public void logout(HttpServletResponse response, String token) {
        formLoginService.logout(response, token);
    }

    @Transactional
    public LoginResponseDto refresh(HttpServletRequest request, HttpServletResponse response) throws CustomException {
        return formLoginService.refresh(request, response);
    }
}
