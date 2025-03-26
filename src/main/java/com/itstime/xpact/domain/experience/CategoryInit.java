package com.itstime.xpact.domain.experience;

import com.itstime.xpact.domain.experience.common.ExperienceType;
import com.itstime.xpact.domain.experience.entity.Category;
import com.itstime.xpact.domain.experience.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CategoryInit {

    private final CategoryRepository categoryRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            if(categoryRepository.count() == 0) {
                List<Category> categoryList = Arrays.stream(ExperienceType.values())
                        .map(name -> Category.builder().name(name.toString()).build())
                        .toList();

                categoryRepository.saveAll(categoryList);
                log.info("Saved {} category", categoryList.size());
            } else {
                log.info("Found {} category", categoryRepository.count());
            }
        };
    }
}
