package com.itstime.xpact.global.auth;

import com.itstime.xpact.domain.member.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class MemberAuthentication extends UsernamePasswordAuthenticationToken {

    private final Member member;

    public MemberAuthentication(Member member, Object credentials) {
        super(member.getId(), credentials); // principal을 memberId로 설정
        this.member = member;
    }

    public MemberAuthentication(Member member, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(member.getId(), credentials, authorities);
        this.member = member;
    }

    @Override
    public Object getCredentials() {
        return super.getCredentials();
    }

    @Override
    public Object getPrincipal() {
        return member.getId();
    }

    public Member getMember() {
        return this.member;
    }

    public static MemberAuthentication createMemberAuthentication(Member member) {

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()));

        return new MemberAuthentication(
                member,
                null,
                authorities
        );
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }
}
