package com.itstime.xpact.infra.lambda.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LambdaResponseDto {
    Integer statusCode;
    List<Body> body;

    @Getter
    public static class Body {
        Integer groupCode;
        String groupName;
        List<DetailRecruitResponseDto> subList;
    }

    @Getter
    public static class DetailRecruitResponseDto {
        Integer subCode;
        String subName;
    }
}
