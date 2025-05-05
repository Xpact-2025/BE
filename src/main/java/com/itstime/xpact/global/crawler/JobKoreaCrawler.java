package com.itstime.xpact.global.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itstime.xpact.global.crawler.dto.RecruitResponseDto;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JobKoreaCrawler {

    private final CrawlerUtil crawlerUtil;
    private final ObjectMapper objectMapper;
    private static final String URL = "https://www.jobkorea.co.kr/recruit/joblist?menucode=duty";

    private WebDriver initWebDriver() {
        WebDriver driver = crawlerUtil.getWebDriver();
        driver.get(URL);
        return driver;
    }

    public List<RecruitResponseDto> crawlRecruits() {
        WebDriver driver = initWebDriver();
        List<WebElement> recruits = driver.findElements(By.cssSelector(".job.circleType.dev-tab.dev-duty .nano-content.dev-main .item"));

        return recruits.stream()
                .map(recruit -> recruit.getAttribute("data-value-json"))
                .filter(Objects::nonNull)
                .map(jsonString -> {
                    try {
                        return objectMapper.readValue(jsonString, RecruitResponseDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
