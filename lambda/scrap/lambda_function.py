import os
import json
import boto3
import datetime

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

def lambda_handler(event, context):
    try:
        new_intern_key = f"data/INTERN_{setTime()}.json"
        intern_data = save_intern_to_s3()
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_intern_key,
            Body=json.dumps(intern_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"INTERN 저장 완료: {new_intern_key}")
    except Exception as e:
        print(f"INTERN 저장 중 오류 발생: {e}")

    try:
        new_competition_key = f"data/COMPETITION_{setTime()}.json"
        competition_data = save_competition_to_s3()
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_competition_key,
            Body=json.dumps(competition_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"COMPETITION 저장 완료: {new_competition_key}")
    except Exception as e:
        print(f"COMPETITION 저장 중 오류 발생: {e}")

    try:
        new_activity_key = f"data/ACTIVITY_{setTime()}.json"
        activity_data = save_activity_to_s3()
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_activity_key,
            Body=json.dumps(activity_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"ACTIVITY 저장 완료: {new_activity_key}")
    except Exception as e:
        print(f"ACTIVITY 저장 중 오류 발생: {e}")

    try:
        new_education_key = f"data/EDUCATION_{setTime()}.json"
        education_data = save_education_to_s3()
        s3.put_object(
            Bucket=BUCKET_NAME,
            Key=new_education_key,
            Body=json.dumps(education_data, indent=2, ensure_ascii=False),
            ContentType="application/json"
        )
        print(f"EDUCATION 저장 완료: {new_education_key}")
    except Exception as e:
        print(f"EDUCATION 처리 중 오류 발생: {e}")

    return {
        'statusCode': 200,
        'body': "크롤링 성공"
    }