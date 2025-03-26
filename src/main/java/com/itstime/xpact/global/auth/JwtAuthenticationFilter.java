package com.itstime.xpact.global.auth;

import com.itstime.xpact.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final MemberRepository memberRepository;

    @Override
    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return super.isAsyncDispatch(request);
    }

    @Override
    protected boolean isAsyncStarted(HttpServletRequest request) {
        return super.isAsyncStarted(request);
    }

    @Override
    protected String getAlreadyFilteredAttributeName() {
        return super.getAlreadyFilteredAttributeName();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return super.shouldNotFilterAsyncDispatch();
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return super.shouldNotFilterErrorDispatch();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    }

    @Override
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterNestedErrorDispatch(request, response, filterChain);
    }
}
