package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CrawlerUtil {

    @Value("${crawler.web-driver-path}")
    private static String WEB_DRIVER_PATH;

    private final Environment environment;

    public WebDriver getWebDriver() {
        String profile = Arrays.stream(environment.getActiveProfiles()).toList().get(0);
        if(profile.equals("dev")) {
            System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        return new ChromeDriver(options);
    }
}
