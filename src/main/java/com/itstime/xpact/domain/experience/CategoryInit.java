package com.itstime.xpact.domain.experience;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Category;
import com.itstime.xpact.domain.experience.repository.CategoryRepository;
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
public class CategoryInit {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {

            long categoryCount = categoryRepository.count();
            if(categoryCount == 0) {
                List<Category> categoryList = Arrays.stream(ExperienceType.values())
                        .map(name -> Category.builder().name(name.toString()).build())
                        .toList();

                categoryRepository.saveAll(categoryList);
                log.info("Saved {} category", categoryList.size());
            } else {
                log.info("Found {} category", categoryRepository.count());
            }

            long memberCount = memberRepository.count();
            if(memberCount == 0) {
                Member memeber = Member.builder()
                        .email("demo")
                        .name("demo")
                        .role(Role.ROLE_USER)
                        .type(Type.FORM)
                        .imgurl("demo")
                        .inactiveDate(LocalDate.now())
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
