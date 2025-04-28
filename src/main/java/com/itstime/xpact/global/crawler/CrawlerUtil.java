package com.itstime.xpact.global.crawler;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlerUtil {

    @Value("${crawler.web-driver-path}")
    private static String WEB_DRIVER_PATH;

    public static WebDriver getWebDriver() {
//        System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);

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
