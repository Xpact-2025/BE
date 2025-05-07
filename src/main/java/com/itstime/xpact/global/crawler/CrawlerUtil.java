package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CrawlerUtil {

    private final Environment environment;

    public WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", "/home/ubuntu/chromedriver-linux64/chromedriver");

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
