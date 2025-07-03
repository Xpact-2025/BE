import os
import json
import boto3
import datetime
from selenium import webdriver
from selenium.webdriver.chrome.service import Service

from crawling_intern import *
from crawling_education import *
from crawling_activity import *
from crawling_competition import *

s3 = boto3.client("s3")
BUCKET_NAME = os.environ.get("S3_BUCKET")
if not BUCKET_NAME:
    raise ValueError("환경 변수 'S3_BUCKET'이 설정되어 있지 않습니다.")

# 파일작성에 필요한 시간 정제 함수
def setTime():
    now = datetime.datetime.now()
    return now.strftime('%Y_%m_%d')

def chrome_driver():
    options = webdriver.ChromeOptions()
    options.binary_location = '/opt/chrome/chrome'
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument("--disable-gpu")
    options.add_argument("--single-process")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument('window-size=1392x1150')
    options.add_argument('user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36')
    driver = webdriver.Chrome(service=Service("/usr/bin/chromedriver"), options=options)

    return driver

def lambda_handler(event, context):
    driver = None

    try:
        driver = chrome_driver()

        # INTERN
        new_intern_key = f"data/INTERN_{setTime()}.json"
        intern_data = save_intern_to_s3(BUCKET_NAME, driver=driver)
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_intern_key,
            Body=json.dumps(intern_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"INTERN 저장 완료: {new_intern_key}")

        # COMPETITION
        new_competition_key = f"data/COMPETITION_{setTime()}.json"
        competition_data = save_competition_to_s3(BUCKET_NAME, driver=driver)
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_competition_key,
            Body=json.dumps(competition_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"COMPETITION 저장 완료: {new_competition_key}")

        # ACTIVITY
        new_activity_key = f"data/ACTIVITY_{setTime()}.json"
        activity_data = save_activity_to_s3(BUCKET_NAME, driver=driver)
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_activity_key,
            Body=json.dumps(activity_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"ACTIVITY 저장 완료: {new_activity_key}")

        # EDUCATION
        new_education_key = f"data/EDUCATION_{setTime()}.json"
        education_data = save_education_to_s3(BUCKET_NAME, driver=driver)
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_education_key,
            Body=json.dumps(education_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"EDUCATION 저장 완료: {new_education_key}")

    except Exception as e:
        print(f"크롤링 중 오류 발생: {e}")

    finally:
        if driver:
            driver.quit()

    return {
        'statusCode': 200,
        'body': "크롤링 성공"
    }