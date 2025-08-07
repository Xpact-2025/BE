from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException, StaleElementReferenceException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import os
import json
import boto3, uuid, shutil

# 전역 세션 ID
def generate_session_id():
    return str(uuid.uuid4())

def chrome_driver(session_id):
    options = webdriver.ChromeOptions()
    options.binary_location = "/opt/chrome/chrome"
    options.add_argument('--no-sandbox')
    options.add_argument("--single-process")
    options.add_argument('--disable-gpu')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument("--remote-debugging-port=9222")
    options.add_argument('--headless')
    options.add_argument('user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36')

    options.add_argument(f"--user-data-dir=/tmp/user-data-{session_id}")
    options.add_argument(f"--data-path=/tmp/data-path-{session_id}")
    options.add_argument(f"--homedir=/tmp/home-{session_id}")
    options.add_argument(f"--disk-cache-dir=/tmp/cache-dir-{session_id}")

    service = Service("/opt/chromedriver")
    driver = webdriver.Chrome(service=service, options=options)

    return driver

def remove_tmp(session_id):
    shutil.rmtree(f"/tmp/user-data-{session_id}", ignore_errors=True)
    shutil.rmtree(f"/tmp/data-path-{session_id}", ignore_errors=True)
    shutil.rmtree(f"/tmp/home-{session_id}", ignore_errors=True)
    shutil.rmtree(f"/tmp/cache-dir-{session_id}", ignore_errors=True)
    

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
                    if res is None:
                        continue

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


# 각각의 link들 크롤링
def get_href(driver):
    links = []
    page_count = 1
    driver.get(f'https://linkareer.com/list/activity?page={page_count}')

    try:
        while True:
            print(f" === ACTIVITY 활동 크롤링 중: {page_count} 페이지 === ")

            cards = WebDriverWait(driver, 10).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "ActivityListCardItem__StyledWrapper-sc-eb439f06-0")))

            for card in cards:
                try:
                    href = card.find_element(By.CLASS_NAME, "image-link").get_attribute('href')
                    links.append(href)
                except StaleElementReferenceException:
                    print("상태 불안정 에러 발생. 건너뜁니다.")
                    continue

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
                    return links

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
        driver.quit()

    return links


# 구체적인 내용 크롤링
def crawling_activity_detail(link: str, driver) -> dict | None:

    # 예외 처리
    def safe_find_text(by, value) -> str | None:
        try:
            return driver.find_element(by, value).text
        except NoSuchElementException:
            return None

    try:
        driver.get(link)
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, 'header.ActivityInformationHeader__StyledWrapper-sc-7bdaebe9-0 h1')))


        linkareer_id = extract_link_id(link)

        title = driver.find_element(By.CSS_SELECTOR, "header h1").text

        img_url = driver.find_element(By.CSS_SELECTOR, 'img.card-image').get_attribute('src')

        organizer_name = safe_find_text(By.CSS_SELECTOR, "h2.organization-name")

        enterprise_type = safe_find_text(By.XPATH, "//dl[dt[text()='기업형태']]/dd")

        job_category = safe_find_text(By.XPATH, "//dl[dt[text()='관심분야']]/dd")

        eligibility = safe_find_text(By.XPATH, "//dl[dt[text()='참여대상']]/dd")

        benefits = safe_find_text(By.XPATH, "//dl[dt[text()='활동혜택']]/dd")

        raw_dates = safe_find_text(By.XPATH, "//dl[dt[text()='접수기간']]/dd")

        lines = raw_dates.split('\n')

        start_date = None
        end_date = None

        if len(lines) == 2:
            for line in lines:
                if '시작일' in line:
                    start_date = line.replace('시작일', '').strip()
                elif '마감일' in line:
                    end_date = line.replace('마감일', '').strip()

        homepage_url = safe_find_text(By.XPATH, "//dl[dt[text()='홈페이지']]/dd")
        if homepage_url in (None, "", "-"):
            homepage_url = link

        return {
            "title": title,
            "linkareer_id": linkareer_id,
            "organizer_name": organizer_name,
            "img_url": img_url,
            "enterprise_type": enterprise_type,
            "job_category": job_category,
            "eligibility": eligibility,
            "benefits": benefits,
            "start_date": start_date,
            "end_date": end_date,
            "homepage_url": homepage_url,
        }

    except Exception as e:
        print(f"상세 페이지 오류: {e}")
        return None


# S3에 저장
def save_activity_to_s3(bucket_name: str, driver, session_id, context):

    # 기존의 데이터 불러오기
    try:
        old_ids = load_all_json_ids_from_s3(bucket_name, "data/ACTIVITY")
    except Exception as e:
        old_ids = set()
        print("파일을 읽을 수 없습니다.", e)

    # 새로운 크롤링
    try:
        count = 0
        links = get_href(driver)
        new_data = []
        for link in links:
            count += 1
            if count % 5 == 0:
                driver.quit()
                remove_tmp(session_id=session_id)
                session_id = generate_session_id()
                driver = chrome_driver(session_id=session_id)

            link_id = extract_link_id(link)
            if link_id is None or link_id in old_ids:
                print(f"이미 존재하는 공고입니다: {link_id}")
                continue

            data = crawling_activity_detail(link, driver)
            if data:
                new_data.append(data)
                print(f"Saved: {data['title']}")

            if context.get_remaining_time_in_millis() < 5000:
                return new_data

        return new_data

    except Exception as e:
        print(f"대외활동 크롤링 중 오류 발생: {e}")