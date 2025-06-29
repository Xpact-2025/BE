import traceback
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import json
import subprocess
import boto3
import os


# 기존의 모든 json 파일 로드
def load_all_json_ids_from_s3(bucket_name: str, prefix: str) -> set:
    s3 = boto3.client("s3")
    BUCKET_NAME = os.environ.get("S3_BUCKET")
    if not BUCKET_NAME:
        raise ValueError("환경 변수 'S3_BUCKET'이 설정되어 있지 않습니다.")

    all_ids = set()

    try:
        response = s3.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
        contents = response.get("Contents", [])

        for obj in contents:
            key = obj["Key"]
            if key.endswith(".json"):
                try:
                    res = s3.get_object(Bucket=bucket_name, Key=key)
                    data = json.load(res["Body"])
                    ids = {item["linkareer_id"] for item in data if "linkareer_id" in item}
                    all_ids.update(ids)
                except Exception as e:
                    print(f"S3에서 {key} 읽는 중 오류: {e}")

    except Exception as e:
        print(f"S3 파일 목록 조회 중 오류: {e}")

    return all_ids

# linkareer_id 추출을 위한 함수
def extract_link_id(link: str) -> int | None:
    prefix = "https://linkareer.com/activity/"
    try:
        return int(link.replace(prefix, ""))
    except ValueError:
        return None


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


def save_competition_to_s3():

    # 기존의 데이터 불러오기
    try:
        old_ids = load_all_json_ids_from_s3(BUCKET_NAME, "data/COMPETITION")
    except Exception as e:
        old_ids = set()
        print("파일을 읽을 수 없습니다.", e)

    new_data = []

    try:
        # 브라우저 꺼짐 방지
        chrome_options = Options()
        chrome_options.binary_location = "/opt/chrome/chrome"
        chrome_options.add_argument("--headless")
        chrome_options.add_argument("--disable-gpu")
        chrome_options.add_argument("--no-sandbox")
        chrome_options.add_argument("--disable-notifications")
        chrome_options.add_argument("--disable-dev-shm-usage")
        chrome_options.add_argument("--single-process")

        service = Service(executable_path="/opt/chromedriver")
        driver = webdriver.Chrome(service=service, options=chrome_options)

        hrefs = get_href(driver=driver)

        for href in hrefs:
            link_id = extract_link_id(href)
            if link_id is None or link_id in old_ids:
                print(f"이미 존재하는 공고입니다: {link_id}")
                continue

            # 새로 크롤링
            print(f"progressing...{href}")
            driver.get(href)
            WebDriverWait(driver, 5).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "activity-detail-content")))

            competition_key = href.split("/")[-1]
            competition_value = competitions_dict.get(competition_key)
            if competition_value is None:
                continue

            competition_value["organizer_name"] = safe_get_text(driver, By.XPATH, "//header[@class='organization-info']/h2[@class='organization-name']")
            competition_value["title"] = safe_get_text(driver, By.XPATH, "//header[contains(@class, 'ActivityInformationHeader__StyledWrapper')]/h1")

            try:
                competition_value["job_category"] = json.dumps([elem.text for elem in driver.find_elements(By.XPATH, "//dt[text()='공모분야']/following-sibling::dd//li/p")], ensure_ascii=False)
            except Exception:
                competition_value["job_category"] = json.dumps([], ensure_ascii=False)

            competition_value["start_date"] = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/div/span[@class='start-at']/following-sibling::span[1]")
            competition_value["end_date"] = safe_get_text(driver, By.XPATH, "//dl[dt[text()='접수기간']]/dd/span[@class='end-at']/following-sibling::span[1]")

            homepage_url = safe_get_attr(driver, By.CSS_SELECTOR, "dd.text > a", "href")
            competition_value["homepage_url"] = homepage_url if homepage_url != '-' else href

            competition_value["img_url"] = safe_get_attr(driver, By.CLASS_NAME, "card-image", "src")
            competition_value["benefits"] = safe_get_text(driver, By.XPATH, "//dt[text()='활동혜택']/following-sibling::dd")
            competition_value["eligibility"] = safe_get_text(driver, By.XPATH, "//dt[text()='참여대상']/following-sibling::dd")
            competition_value["linkareer_id"] = int(competition_key)

            new_data.append(competition_value)
            print(f"Saved: {competition_value['title']}")

    finally:
        driver.quit()
