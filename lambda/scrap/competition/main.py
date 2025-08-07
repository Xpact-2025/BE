import os
import json
import boto3
import datetime

from crawling_competition import *

s3 = boto3.client("s3")
BUCKET_NAME = os.environ.get("S3_BUCKET")
if not BUCKET_NAME:
    raise ValueError("환경 변수 'S3_BUCKET'이 설정되어 있지 않습니다.")

def safe_driver_get(driver, url):
    try:
        driver.get(url)
    except Exception as e:
        print(f"driver.get() 실패, 세션 재생성 중: {e}")
        try:
            driver.quit()
        except:
            pass
        driver = chrome_driver()
        driver.get(url)
    return driver

# 파일작성에 필요한 시간 정제 함수
def setTime():
    now = datetime.datetime.now()
    return now.strftime('%Y_%m_%d')

def lambda_handler(event, context):
    driver = None

    try:
        session_id = generate_session_id()
        driver = chrome_driver(session_id=session_id)

        # COMPETITION
        new_competition_key = f"data/COMPETITION_{setTime()}.json"
        competition_data = save_competition_to_s3(BUCKET_NAME, driver=driver, session_id=session_id, context=context)
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_competition_key,
            Body=json.dumps(competition_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"COMPETITION 저장 완료: {new_competition_key}")

    except Exception as e:
        print(f"크롤링 중 오류 발생: {e}")

    finally:
        try:
            driver.stop_client()
            driver.close()
            driver.quit()
        except:
            pass

    return {
        'statusCode': 200,
        'body': "크롤링 성공"
    }