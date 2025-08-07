package com.itstime.xpact.infra.s3.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.domain.guide.common.ScrapType;
import com.itstime.xpact.domain.guide.dto.request.ScrapRequestDto;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.global.auth.SecurityProvider;
import com.itstime.xpact.global.exception.ErrorCode;
import com.itstime.xpact.global.exception.GeneralException;
import com.itstime.xpact.infra.s3.dto.FileResponseDto;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final SecurityProvider securityProvider;

    private static final String prefix = "USER_UPLOADS";
    private static final String S3_PREFIX = "data/";
    private static final String DEFAULT_PROFILE_IMAGE = prefix + "/defaults/DEFAULT_PROFILE.png";
    private static final String PROFILE_PREFIX = "/profiles/";
    private static final String REGION = "ap-northeast-2";

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

    public List<ScrapRequestDto> findCrawlingFile(ScrapType scrapType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        List<ScrapRequestDto> results = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            LocalDate targetDate = LocalDate.now().minusDays(i);
            String formattedDate = targetDate.format(formatter);

            String key = S3_PREFIX + scrapType.name().toUpperCase() + "_" + formattedDate + ".json";
            System.out.println("key = " + key);
            try {
                GetObjectRequest getRequest = GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

                ResponseInputStream<GetObjectResponse> file = s3Client.getObject(getRequest);
                List<ScrapRequestDto> data = objectMapper.readValue(file, new TypeReference<List<ScrapRequestDto>>() {});
                results.addAll(data);
            } catch (S3Exception e) {
                log.error(e.getMessage());
                log.info("파일을 찾을 수 없습니다.");
            } catch (Exception e) {
                log.error("파일을 파싱할 수 없습니다.");
            }
        }

        return results;
    }


    // 프로필 이미지
    public FileResponseDto getPreSignedProfileUrl(@Nullable String fileName) {
        Member member = securityProvider.getCurrentMember();

        if (fileName == null || fileName.isBlank()) {

            return FileResponseDto.of(null, getS3Url(DEFAULT_PROFILE_IMAGE));
        }

        String key = prefix + "/" + member.getId() + PROFILE_PREFIX + "profile.png";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                b -> b.putObjectRequest(putObjectRequest)
                        .signatureDuration(Duration.ofMinutes(5)));

        String imageUrl = getS3Url(key);

        member.setImageUrl(imageUrl);
        return FileResponseDto.of(presignedRequest.url().toString(), imageUrl);
    }

    private String getS3Url(String key) {
        return "https://" + bucket + ".s3." + REGION + ".amazonaws.com/" + key;
    }
}
