package com.itstime.xpact.global.crawler.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RecruitResponseDto {
    Integer groupCode;
    String groupName;
    List<DetailRecruitResponseDto> subList;

    @Getter
    public static class DetailRecruitResponseDto {
        Integer subCode;
        String subName;
    }
}
