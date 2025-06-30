import os
import traceback
from dotenv import load_dotenv
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from webdriver_manager.chrome import ChromeDriverManager

import boto3, json
from datetime import datetime


def safe_get_text(driver, by, value):
    try:
        return driver.find_element(by, value).text
    except NoSuchElementException:
        return '-'

def safe_get_attr(driver, by, value, attr):
    try:
        return driver.find_element(by, value).get_attribute(attr)
    except NoSuchElementException:
        return '-'


educations_dict = dict()
load_dotenv()
s3 = boto3.client("s3")
BUCKET_NAME = os.getenv("S3_BUCKET")


def get_href(driver):
    results = []
    page_count = 1
    driver.get(f'https://linkareer.com/list/education?page={page_count}')

    try:
        while(True):
            print(f" === EDUCATION 활동 크롤링 중: {page_count} 페이지 === ")
            cards = WebDriverWait(driver, 15).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "EducationListRow-desktop__StyledWrapper-sc-1c4f824c-0")))
            # 각 ACTIVITY당 썸네일에서 파싱 가능한 데이터는 미리 dictionary에 삽입 (key, value) -> 여기서 key값은 href의 마지막 숫자 ex) 243667, value는 dict()
            for card in cards:
                href = card.find_element(By.CLASS_NAME, "education-link").get_attribute("href")
                results.append(href)
                education_key = href.split("/")[-1]
                educations_dict[education_key] = dict()


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

def get_new_data(hrefs: list):
    now = datetime.now()
    formatted = now.strftime("%Y-%m-%d")
    key = f"data/EDUCATION-{formatted}.json"
    response = s3.get_object(Bucket=BUCKET_NAME, Key=key)
    body = response['Body'].read().decode('utf-8')
    contents = json.loads(body)
    existing_ids = [content["linkareer_id"] for content in contents]
    return [href for href in hrefs if href.split("/")[-1] not in existing_ids]

def put_new_data(datas: list):
    now = datetime.now()
    formatted = now.strftime("%Y-%m-%d")
    key = f"data/EDUCATION-{formatted}.json"
    body = json.dumps(datas, indent=2)
    s3.put_object(
        Bucket=BUCKET_NAME,
        Key=key,
        Body=body.encode("utf-8"),
        ContentType="application/json"
    )
    return key

def lambda_handler():
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

        # 링크 중 이미 존재하는 데이터가 있으면 제외
        hrefs = get_new_data(hrefs)

        for href in hrefs:
            print(f"progressing...{href}")
            driver.get(href)
            WebDriverWait(driver, 5).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "activity-detail-content")))

            competition_key = href.split("/")[-1]
            education_value = educations_dict[competition_key]

            # 만약 데이터가 없을 시, pass
            if(education_value is None):
                continue

            education_value["organizer_name"] = safe_get_text(driver, By.XPATH, "//header[@class='organization-info']/h2[@class='organization-name']")
            education_value["title"] = safe_get_text(driver, By.XPATH, "//header[contains(@class, 'ActivityInformationHeader__StyledWrapper')]/h1")

            # start_date 날짜 형식이 아닐 시 None 입력
            start_date = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/div/span[@class='start-at']/following-sibling::span[1]").strip()
            education_value["start_date"] = start_date

            # end_date 날짜 형식이 아닐 시 None 입력
            end_date = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/span[@class='end-at']/following-sibling::span[1]").strip()
            education_value["end_date"] = end_date

            education_value["job_category"] = safe_get_text(driver, By.XPATH, "//dt[text()='교육분야']/following-sibling::dd[1]")

            # 홈페이지 없을 시, 링커리어 링크 첨부
            homepage_url = safe_get_attr(driver, By.CSS_SELECTOR, "dd.text > a", "href")
            if(homepage_url == '-'):
                homepage_url = href
            education_value["homepage_url"] = homepage_url

            education_value["img_url"] = safe_get_attr(driver, By.CLASS_NAME, "card-image", "src")
            education_value["on_off_line"] = safe_get_text(driver, By.XPATH, "//dt[text()='온/오프라인']/following-sibling::dd[1]")
            education_value["linkareer_id"] = competition_key

        # educatio_dict에 {key : {}}로 들어있음 -> values() 사용
        data_file = [v for v in educations_dict.values() if v]
        key = put_new_data(data_file)
    finally:
        driver.quit()

    return {
        "statusCode": 200,
        "body": json.dumps({"message": "Saved to S3", "s3_key": key})
    }


lambda_handler()