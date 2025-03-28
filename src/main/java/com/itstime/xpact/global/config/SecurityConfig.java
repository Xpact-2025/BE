package com.itstime.xpact.global.config;

import com.itstime.xpact.domain.member.repository.MemberRepository;
import com.itstime.xpact.global.auth.JwtAuthenticationFilter;
import com.itstime.xpact.global.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(httpSecurityCorsConfigurer ->
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler()));

        setTokenFilter(httpSecurity);
        setPermission(httpSecurity);

        return httpSecurity.build();
    }

    private void setTokenFilter(HttpSecurity httpSecurity) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(memberRepository, tokenProvider);
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();

        conf.setAllowedOrigins(List.of("http://localhost:3000"));
        conf.setAllowedMethods(List.of("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        conf.setAllowCredentials(true);
        conf.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);

        return source;
    }

    private void setPermission(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()  // 추후에 접근 설정 추가
                .anyRequest().authenticated()
        );
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        // Custom AuthenticaitonSuccessHandler 설정
        return (request, response, authentication) -> {
            // 로그인 성공 시 수행할 로직
            response.sendRedirect("/success"); // 로그인 성공 시 리다이렉트
        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        // Custom AuthenticationFailureHandler 설정
        return (request, response, authentication) -> {
            // 로그인 실패 시 수행할 로직
            response.sendRedirect("/failure");
        };
    }
 }
