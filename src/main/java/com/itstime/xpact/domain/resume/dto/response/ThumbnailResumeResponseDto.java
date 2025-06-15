package com.itstime.xpact.domain.resume.dto.response;

import com.itstime.xpact.domain.resume.entity.Resume;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ThumbnailResumeResponseDto {
    private Long id;
    private String title;
    private LocalDate createDate;

    public static ThumbnailResumeResponseDto of(Resume resume) {
        return ThumbnailResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .createDate(resume.getCreatedTime().toLocalDate())
                .build();
    }
}
