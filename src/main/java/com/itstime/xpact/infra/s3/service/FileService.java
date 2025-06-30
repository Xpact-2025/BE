package com.itstime.xpact.infra.s3.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.dto.ScrapResponseDto;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.infra.s3.dto.FileResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final SecurityProvider securityProvider;
    private static final String prefix = "USER_UPLOADS";

    @Value("${aws.credentials.bucket}")
    private String bucket;


    public FileResponseDto getPreSignedUrl(String fileName) {
        // key 형식 : USER_UPLOADS/{member_id}/{UUID}_{file_name}
        // ex) USER_UPLOADS/2/awsfqwvawerfr239rey29fbwiuf9_증명서.pdf
        String key = prefix + "/" +
                securityProvider.getCurrentMember().getId().toString() + "/" +
                UUID.randomUUID() + "_" + fileName;

        PutObjectRequest putObject= PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        log.info("key = {}", key);
        PresignedPutObjectRequest request = s3Presigner.presignPutObject(
                b -> b.putObjectRequest(putObject).signatureDuration(Duration.ofMinutes(5)));

        return FileResponseDto.of(request.url().toString(), key);
    }

    public String getFileUrl(String key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.headObject(headRequest); // 존재하지 않으면 예외 발생
        } catch (NoSuchKeyException e) {
            throw GeneralException.of(ErrorCode.NO_SUCH_FILE);
        }


        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(
                b -> b.signatureDuration(Duration.ofMinutes(5))
                        .getObjectRequest(getRequest));

        return presigned.url().toString();
    }

    public List<ScrapResponseDto> findCrawlingFile(ScrapType scrapType) {
        String key = scrapType.name();

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key("data/" + key + ".json")
                .build();

        // 파일 존재 시 -> json으로 파싱 -> 자바 클래스로 파싱하여 return
        // 파일 존재하지 않을 시 -> return;
        try{
            ResponseInputStream<GetObjectResponse> file = s3Client.getObject(getRequest);
            return objectMapper.readValue(file, new TypeReference<List<ScrapResponseDto>>() {});
        } catch (S3Exception e) {
            log.info("파일을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("파일을 파싱할 수 없습니다.");
        }

        return List.of();
    }
}
