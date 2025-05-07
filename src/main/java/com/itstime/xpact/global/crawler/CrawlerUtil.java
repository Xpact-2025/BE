package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class CrawlerUtil {

    @Value("${selenium-url}")
    private String seleniumUrl;

    private final Environment environment;

    public WebDriver getWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");

        if(Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            try {
                return new RemoteWebDriver(new URL(seleniumUrl), options);
            } catch (Exception e) {
                throw new RuntimeException("failed to connect to remotewebdriver", e);
            }
        } else {
            return new ChromeDriver(options);
        }
    }
}
