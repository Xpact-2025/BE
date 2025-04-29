package com.itstime.xpact.global.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${external.api.school.base-url}")
    private String baseUrl;

    // Trie Config
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<String>();
    }

    // TODO : WebClient Bean 생성
    @Bean
    @Qualifier("xmlWebClient")
    public WebClient xmlWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs()
                            .jaxb2Encoder(new Jaxb2XmlEncoder());
                    clientCodecConfigurer.defaultCodecs()
                            .jaxb2Decoder(new Jaxb2XmlDecoder());
                })
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(logRequest())
                .uriBuilderFactory(factory)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }
}
