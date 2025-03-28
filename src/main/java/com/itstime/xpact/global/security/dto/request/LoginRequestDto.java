package com.itstime.xpact.global.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record LoginRequestDto (
        String email,
        String password
) {
}
