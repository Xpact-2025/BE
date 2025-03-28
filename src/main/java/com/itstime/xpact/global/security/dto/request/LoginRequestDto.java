package com.itstime.xpact.global.security.dto.request;

public record LoginRequestDto (
        String email,
        String password
) {
}
