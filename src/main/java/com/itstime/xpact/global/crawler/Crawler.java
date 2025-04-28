package com.itstime.xpact.global.crawler;

import com.itstime.xpact.domain.recruit.dto.DetailRecruitDto;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Crawler {

    private static final String URL = "https://job.incruit.com/jobdb_list/searchjob.asp?cate=occu";

    private WebDriver initWebDriver() {
        WebDriver driver = CrawlerUtil.getWebDriver();
        driver.get(URL);

        WebElement banner = driver.findElement(By.className("rubaner-close"));
        banner.click();

        List<WebElement> depth1List = driver.findElements(By.name("occ1_list"));
        for (WebElement depth1 : depth1List) {
            refresh(driver);
            depth1.click();
        }
        return driver;
    }

    private static void refresh(WebDriver driver) {
        WebElement refreshBtn = driver.findElement(By.className("shb-btn-ref"));
        refreshBtn.click();
    }

    public List<String> crawlRecruits() {
        WebDriver driver = initWebDriver();

        // depth2 조회
        List<WebElement> depth2List = driver.findElements(By.name("occ2_list"));
        List<String> depth2NameList = new ArrayList<>();
        for (WebElement depth2 : depth2List) {
            String name = depth2.getAttribute("data-item-name");
            depth2NameList.add(name);
        }

        return depth2NameList;
    }

    public List<DetailRecruitDto> crawlDetailRecruits() {
        WebDriver driver = initWebDriver();

        // depth2 조회
        List<WebElement> depth2List = driver.findElements(By.name("occ2_list"));
        List<String> depth2IdList = new ArrayList<>();

        for (WebElement depth2 : depth2List) {
            String id = depth2.getAttribute("id");

            assert id != null;
            String newId = id.replace("occ2_list", "occ1_list").substring(0, id.lastIndexOf("_"));
            driver.findElement(By.id(newId)).click();
            refresh(driver);

            WebElement container = driver.findElement(By.id("occ2_div"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[1].offsetTop;", container, depth2);
            depth2.click();
            depth2IdList.add(id);
        }
        depth2List.clear();

        List<String> depth3NameList = depth2IdList.stream()
                .map(id -> id.substring(0, 3) + "3" + id.substring(4))
                .toList();

        depth2IdList.clear();

        List<DetailRecruitDto> detailRecruitDtos = new ArrayList<>();
        for (String name : depth3NameList) {
            List<WebElement> depth3List = driver.findElements(By.name(name));

            for (WebElement depth3 : depth3List) {
                String recruit = depth3.getAttribute("data-item-upstr");
                String detailRecruit = depth3.getAttribute("data-item-name");
                detailRecruitDtos.add(new DetailRecruitDto(recruit, detailRecruit));
            }
            depth3List.clear();
        }
        return detailRecruitDtos;
    }
}
