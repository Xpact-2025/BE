package com.itstime.xpact.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    // 서버에서 HTTP 요청을 보내기 위한 템플릿 설정
    // TODO : 비동기 처리 및 확장성을 위하여 WebClient로의 확장도 고려
    // TODO : Feign Client를 자주 사용하는 MSA 구조에 대하여 고려해보기

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }
}
