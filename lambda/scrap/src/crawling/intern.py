from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException, ElementClickInterceptedException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from webdriver_manager.chrome import ChromeDriverManager

import time
import json

results = []


def crawling_intern():
    main_url = 'https://linkareer.com/list/intern'

    # 브라우저 꺼짐 방지
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_experimental_option('detach', True)
    User_Agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"
    chrome_options.add_argument(f"user-agent={User_Agent}")

    # 불필요한 에러 X
    chrome_options.add_experimental_option('excludeSwitches', ['enable-logging'])

    # 크롬 드라이버 경로
    driver_path = "C:/Windows/chromedriver.exe"

    # 크롬 드라이버 생성 (자동 업데이트도 같이)
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

    results = []

    while True:
        try:
            driver.get(main_url)
            time.sleep(3)

            page = 1

            print(f"크롤링 중: {page} 페이지")

            rows = driver.find_elements(By.CSS_SELECTOR, 'tbody tr')
            for row in rows:
                tds = row.find_elements(By.TAG_NAME, 'td')
                if len(tds) >= 6:
                    organizer_name = tds[0].text.strip()
                    title_cell = tds[1]
                    try:
                        link = title_cell.find_element(By.TAG_NAME, 'a').get_attribute('href')
                        title = title_cell.find_element(By.CSS_SELECTOR, 'p.recruit-name').text.strip()
                    except:
                        title = None

                    try:
                        job_category = title_cell.find_element(By.CSS_SELECTOR, 'p.recruit-category').text.strip()
                    except:
                        category = None

                    work_type = tds[2].text.strip()
                    region = tds[3].text.strip()
                    deadline = tds[4].text.strip()

                    results.append({
                        "organizer_name":organizer_name,
                        "title":title,
                        "job_category":job_category,
                        "work_type":work_type,
                        "region":region,
                    })

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

            # 저장
            for result in results:
                save_to_db(result)
            print(f"총 {page} 페이지 크롤링 완료.")

        except Exception as e:
            print(e)

        finally:
            driver.quit()

def save_to_db(raw: dict) -> None:
    from src.database.orm import Scrap, ScrapType
    from datetime import datetime
    scrap = Scrap(
        scrap_type = ScrapType.INTERN,

        title=raw["title"],
        organizer_name=raw["organizer_name"],

        work_type=raw["work_type"],
        job_category=raw["job_category"],
        region=raw["region"]
    )
    from src.database.connection import get_session

    with get_session() as session:
        session.add(scrap)

crawling_intern()