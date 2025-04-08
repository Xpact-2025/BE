package com.itstime.xpact.global.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class ApiConfig {

    // Trie Config
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<String>();
    }

    // Encode 모드로 설정
    @Bean
    public DefaultUriBuilderFactory builderFactory() {
        DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory();
        builderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        return builderFactory;
    }

    @Bean
    public WebClient.Builder webClient() {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

    }
}
