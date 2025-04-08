package com.itstime.xpact.global.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public WebClient webClient() {
        return WebClient.builder()
                .uriBuilderFactory(builderFactory())
                .build();
    }
}
