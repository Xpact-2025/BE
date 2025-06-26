import traceback
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from webdriver_manager.chrome import ChromeDriverManager


import json, re

def safe_get_text(driver, by, value):
    try:
        print(driver.find_element(by, value).text)
        return driver.find_element(by, value).text
    except NoSuchElementException:
        return '-'

def safe_get_attr(driver, by, value, attr):
    try:
        print(driver.find_element(by, value).get_attribute(attr))
        return driver.find_element(by, value).get_attribute(attr)
    except NoSuchElementException:
        return '-'


competitions_dict = dict()


def get_href(driver):
    results = []
    page_count = 1
    driver.get(f'https://linkareer.com/list/contest?filterType=CATEGORY&orderBy_direction=DESC&orderBy_field=CREATED_AT&page={page_count}')

    try:
        while(True):
            print(f" === 크롤링 중: {page_count} 페이지 === ")
            cards = WebDriverWait(driver, 15).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "activity-list-card-item-wrapper")))
            # 각 ACTIVITY당 썸네일에서 파싱 가능한 데이터는 미리 dictionary에 삽입 (key, value) -> 여기서 key값은 href의 마지막 숫자 ex) 243667, value는 dict()
            for card in cards:
                href = card.find_element(By.CLASS_NAME, "image-link").get_attribute("href")
                results.append(href)
                competition_key = href.split("/")[-1]
                competitions_dict[competition_key] = dict()


            buttons = WebDriverWait(driver, 10).until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".button-page-number")))

            # 현재 페이지 목록에서 최대 페이지 번호
            max_number = 0
            for btn in buttons:
                max_number = max(max_number, int(btn.text.strip()))

            # 현재 위치해있는 페이지와 페이지 번호
            active_button = WebDriverWait(driver, 2).until(EC.presence_of_element_located((By.CSS_SELECTOR, ".active-page")))
            active_number = int(active_button.text.strip())

            # 다음 페이지 버튼 눌러야 할 때 (최대 페이지 번호와 현재 페이지 번호가 같을 때)
            if(active_number == max_number):
                next_button = driver.find_element(By.CLASS_NAME, "button-arrow-next")
                driver.execute_script("arguments[0].scrollIntoView(true);", next_button)

                # 만약 마지막 페이지 번호라 다음 목록으로 넘어가는 버튼이 활성화되지 않았다면 종료
                if "Mui-disabled" in next_button.get_attribute("class"):
                    return results

                driver.execute_script("arguments[0].click();", next_button)

            # 페이지 목록을 조회 후, 다음 눌러야할 번호가 같으면 클릭
            buttons = driver.find_elements(By.CSS_SELECTOR, ".button-page-number")
            for btn in buttons:
                if btn.text.strip() == str(active_number + 1):
                    driver.execute_script("arguments[0].click();", btn)
                    break

            # 다음 페이지 넘어간 후 페이지 번호 1 증가
            page_count = active_number + 1
    except Exception as e:
        print(e)
        traceback.print_exc()  # 추가
        driver.quit()


def crawling_activities():
    # 브라우저 꺼짐 방지
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_experimental_option('detach', True)
    User_Agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"
    chrome_options.add_argument(f"user-agent={User_Agent}")
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-notifications")
    chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

    try:
        hrefs = get_href(driver=driver)

        for href in hrefs:
            print(f"progressing...{href}")
            driver.get(href)
            WebDriverWait(driver, 5).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "activity-detail-content")))

            competition_key = href.split("/")[-1]
            competition_value = competitions_dict[competition_key]

            # 만약 데이터가 없을 시, pass
            if(competition_value is None):
                continue

            competition_value["organizer_name"] = safe_get_text(driver, By.XPATH, "//header[@class='organization-info']/h2[@class='organization-name']")
            competition_value["title"] = safe_get_text(driver, By.XPATH, "//header[contains(@class, 'ActivityInformationHeader__StyledWrapper')]/h1")
            competition_value["job_category"] = safe_get_text(driver, By.XPATH, "//dt[text()='공모분야']/following-sibling::dd//li/p")

            start_date = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/div/span[@class='start-at']/following-sibling::span[1]")
            if not re.fullmatch(r"\d{4}[-.]\d{2}[-.]\d{2}", start_date):
                start_date = None
            competition_value["start_date"] = start_date

            end_date = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/span[@class='end-at']/following-sibling::span[1]")
            if not re.fullmatch(r"\d{4}[-.]\d{2}[-.]\d{2}", end_date):
                end_date = None
            competition_value["end_date"] = end_date

            try:
                competition_value["job_category"] = json.dumps([elem.text for elem in driver.find_elements(By.XPATH, "//dt[text()='공모분야']/following-sibling::dd//li/p")], ensure_ascii=False)
            except Exception:
                competition_value["job_category"] = json.dumps([], ensure_ascii=False)

            # 홈페이지 없을 시, 링커리어 링크 첨부
            homepage_url = safe_get_attr(driver, By.CSS_SELECTOR, "dd.text > a", "href")
            if(homepage_url == '-'):
                homepage_url = href
            competition_value["homepage_url"] = homepage_url

            competition_value["img_url"] = safe_get_attr(driver, By.CLASS_NAME, "card-image", "src")
            competition_value["benefits"] = safe_get_text(driver, By.XPATH, "//dt[text()='활동혜택']/following-sibling::dd")
            competition_value["eligibility"] = safe_get_text(driver, By.XPATH, "//dt[text()='참여대상']/following-sibling::dd")
            competition_value["linkareer_id"] = competition_key
            print(competition_value)
            if(find_by_reference_url(href)):
                return
            else:
                save_to_db(competition_value)
    finally:
        driver.quit()

# linkareer_id로 레코드 확인 있으면 true, 없으면 false 반환
def find_by_reference_url(url: str) -> bool:
    from src.database.connection import get_session
    from src.database.orm import Scrap

    with get_session() as session:
        return session.query(Scrap).filter(Scrap.homepage_url == url).first() is not None

def save_to_db(raw: dict) -> None:
    from src.database.orm import Scrap, ScrapType
    from datetime import datetime

    scrap = Scrap(
        scrap_type = ScrapType.COMPETITION,

        created_time = datetime.now(),
        modified_time = datetime.now(),
        linkareer_id = raw.get("linkareer_id"),
        title = raw.get("title"),
        organizer_name = raw.get("organizer_name"),
        start_date = raw.get("start_date"),
        end_date = raw.get("end_date"),
        job_category = raw.get("job_category"),
        homepage_url = raw.get("homepage_url"),
        img_url = raw.get("img_url"),
        benefits = raw.get("benefits"),
        eligibility = raw.get("eligibility")
    )

    from src.database.connection import get_session
    with get_session() as session:
        session.add(scrap)
        session.commit()


crawling_activities()