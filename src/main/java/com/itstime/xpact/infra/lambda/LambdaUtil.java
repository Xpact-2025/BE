package com.itstime.xpact.infra.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LambdaUtil {

    private final ObjectMapper objectMapper;

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public List<LambdaResponseDto.Body> invokeLambda(LambdaClient lambdaClient, String functionName) {
        InvokeResponse res;
        try {
            //Setup an InvokeRequest
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .build();

            res = lambdaClient.invoke(request);
            String payload = res.payload().asUtf8String();
            LambdaResponseDto responseDto = objectMapper.readValue(payload, LambdaResponseDto.class);
            return responseDto.getBody();

        } catch(LambdaException | JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw CustomException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
