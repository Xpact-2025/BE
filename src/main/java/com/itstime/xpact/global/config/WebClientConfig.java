package com.itstime.xpact.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${external.api.school.base-url}")
    private String schoolBaseUrl;

    @Bean
    public WebClient schoolWebClient() {

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                    clientCodecConfigurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                })
                .build();

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(schoolBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient.builder()
                .filter(logRequest())
                .uriBuilderFactory(factory)
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader("Accept", MediaType.APPLICATION_XML_VALUE)
                .build();
    }

    // Kakao
    public WebClient buildKakaoWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                // KAKAO open API 필수 Header
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .build();
    }

    // WebClient 요청에서의 log
    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }
}
