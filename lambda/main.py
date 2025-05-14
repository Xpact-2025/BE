from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
import json


def lambda_handler(event, context):
    chrome_options = Options()
    chrome_options.binary_location = "/opt/chrome/chrome"
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--single-process")

    service = Service(executable_path="/opt/chromedriver")

    driver = webdriver.Chrome(
        service=service,
        options=chrome_options
    )

    driver.get("https://www.jobkorea.co.kr/recruit/joblist?menucode=duty")
    recruits = driver.find_elements(By.CSS_SELECTOR, ".job.circleType.dev-tab.dev-duty .nano-content.dev-main .item")

    results = []
    for recruit in recruits:
        json_str = recruit.get_attribute("data-value-json")
        if json_str:
            try:
                data = json.loads(json_str)
                results.append(data)
            except json.JSONDecodeError:
                pass

    driver.quit()

    return {
        'statusCode': 200,
        'body': results
    }
