package com.itstime.xpact.global.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record LoginRequestDto (
        @Schema(description = "이메일",
                example = "itta@example.com")
        String email,
        String password
) {
}
