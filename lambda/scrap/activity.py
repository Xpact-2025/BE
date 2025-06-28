from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException, ElementClickInterceptedException

from webdriver_manager.chrome import ChromeDriverManager

import time
import json
import boto3

from dotenv import load_dotenv



def crawling_activity_link():
    main_url = 'https://linkareer.com/list/activity'

    driver.get(main_url)
    time.sleep(3)

    links = []

    try:
        page = 1
        while True:
            print(f"크롤링 중: {page} 페이지")

            contents = driver.find_elements(By.CSS_SELECTOR, 'div[class^="activity-list-card-item-wrapper"]')

            for content in contents:
                href = content.find_element(By.CSS_SELECTOR, 'a[href^="/activity/"]').get_attribute('href')
                full_link = "https://linkareer.com" + href if href.startswith("/") else href

                links.append(full_link)

            # 다음 페이지
            try:
                next_page_number = str(page + 1)
                next_page_button = driver.find_element(
                    By.XPATH, f'//button[span[text()="{next_page_number}"]]'
                )
                next_page_button.click()
                page += 1
                time.sleep(3)

            except NoSuchElementException:
                try:
                    next_arrow = driver.find_element(By.CSS_SELECTOR, 'button.button-arrow-next')
                    if next_arrow.get_attribute("disabled"):
                        print("마지막 페이지입니다.")
                        break
                    next_arrow.click()
                    page += 1
                    time.sleep(3)
                except NoSuchElementException:
                    print("다음 버튼 없음. 종료합니다.")
                    break
                except ElementClickInterceptedException:
                    print("화살표 클릭 차단됨.")
                    break
            except ElementClickInterceptedException:
                print("페이지 번호 클릭 차단됨.")
                break

    except Exception as e:
        print(e)

    finally:
        driver.quit()
        return links


# 구체적인 내용 크롤링
def crawling_activity_detail(link: str) -> dict | None:
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
    # 크롬 드라이버 생성 (자동 업데이트도 같이)
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

    # 예외 처리
    def safe_find_text(by, value) -> str | None:
        try:
            return driver.find_element(by, value).text
        except NoSuchElementException:
            return None

    try:
        driver.get(link)
        time.sleep(3)

        title = safe_find_text(By.CSS_SELECTOR, 'header.ActivityInformationHeader__StyledWrapper-sc-7bdaebe9-0 h1')

        prefix = "https://linkareer.com/activity/"
        linkareer_id = int(link.replace(prefix, ""))

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
    finally:
        driver.quit()


# S3에 저장
def lambda_handler(event, context):

    load_dotenv()

    s3 = boto3.client("s3")

    BUCKET_NAME = os.etenv("S3_BUCKET")

    key = f"data/activity.json"

    try:
        old_data = s3.get_object(Bucket=BUCKET_NAME, Key=key)
    except Exception as e:
        old_data = []
        print("파일을 읽을 수 없습니다.", e)

    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_experimental_option('detach', True)
    User_Agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"
    chrome_options.add_argument(f"user-agent={User_Agent}")
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-notifications")
    # 불필요한 에러 X
    chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])
    # 크롬 드라이버 생성 (자동 업데이트도 같이)
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

    links = crawling_activity_link(driver)

    for link in links:
        if int(link.replace("https://linkareer.com/activity/", "")) == old_data["linkareer_id"]:
            continue
        else:
            crawling_activity_detail(link, driver)

    s3.put_object(
        Bucket=BUCKET_NAME,
        Key=key,
        Body=json.dumps(new_data)
    )
    return {
        "statusCode": 200,
        "body": json.dumps({"message": "Saved to S3", "s3_key": key})
    }