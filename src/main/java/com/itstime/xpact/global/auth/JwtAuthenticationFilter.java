package com.itstime.xpact.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.response.RestResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        System.out.println("Request path: " + path);
        boolean shouldNotFilter = path.startsWith("/auth/signup") ||
                path.startsWith("/auth/login/") ||
                path.startsWith("/auth/url/") ||
                path.startsWith("/v3/") ||
                path.startsWith("/swagger-ui/");
        System.out.println("Should not filter: " + shouldNotFilter);
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        // Verify
        try {
            // Request Header에서 JWT token 추출
            String token = getJwtFromReqeust(request);
            RestResponse<?> result = tokenProvider.validationToken(token);

            // JWT Validate
            if (StringUtils.hasText(token) && result.getHttpStatus() == 200) {

                Long memberId = tokenProvider.getMemberIdFromToken(token);

                // ID(PK)를 통한 인증 생성
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> CustomException.of(ErrorCode.MEMBER_NOT_EXISTS));

                MemberAuthentication authentication = MemberAuthentication.createMemberAuthentication(member);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ErrorCode errorCode = ErrorCode.INVALID_JWT_TOKEN;
            Map<String, Object> errorBody = new HashMap<>();

            errorBody.put("httpStatus", errorCode.getHttpStatus());
            errorBody.put("code", errorCode.getCode());
            errorBody.put("message", e.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorBody));
            log.warn("JWT 인증 실패");

            return;
        }

        filterChain.doFilter(request, response);
    }

    // 토큰 추출
    private String getJwtFromReqeust(
            @NotNull HttpServletRequest request) {

        // Header에서 "Authorization" 추출
        String bearerToken = request.getHeader(TOKEN_HEADER);

        if (bearerToken != null) {
            String token = bearerToken.startsWith(TOKEN_PREFIX) ? bearerToken.substring(TOKEN_PREFIX.length()) : bearerToken;

            // Null 값일 경우
            if (!StringUtils.hasText(token)) {
                log.warn("Authorization header is missing or empty");
                return null;
            }

            log.info("입력 받은 토큰 : {" + token + "}");
            return token;
        }
        return null;
    }
}
