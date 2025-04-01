package com.itstime.xpact.domain.experience;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.member.common.Role;
import com.itstime.xpact.domain.member.common.Type;
import com.itstime.xpact.domain.member.entity.Member;
import com.itstime.xpact.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInit {

    private final MemberRepository memberRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            // 임시 Member 생성
            long memberCount = memberRepository.count();
            if(memberCount == 0) {
                Member memeber = Member.builder()
                        .email("test@test.com")
                        .name("test")
                        .role(Role.ROLE_ADMIN)
                        .type(Type.FORM)
                        .birthDate(LocalDate.now())
                        .build();

                memberRepository.save(memeber);
                log.info("Saved {} member", memberRepository.count());
            } else {
                log.info("Found {} member", memberRepository.count());
            }
        };
    }
}
