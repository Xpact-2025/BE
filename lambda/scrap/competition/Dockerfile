FROM --platform=linux/amd64 public.ecr.aws/lambda/python:3.13 AS build

# install unzip, install chrome & webdriver -> /opt에 위치하도록 설정
RUN dnf install -y unzip && \
    curl -Lo "/tmp/chromedriver-linux64.zip" "https://storage.googleapis.com/chrome-for-testing-public/136.0.7103.92/linux64/chromedriver-linux64.zip" && \
    curl -Lo "/tmp/chrome-linux64.zip" "https://storage.googleapis.com/chrome-for-testing-public/136.0.7103.92/linux64/chrome-linux64.zip" && \
    unzip /tmp/chromedriver-linux64.zip -d /opt/ && \
    unzip /tmp/chrome-linux64.zip -d /opt/

FROM --platform=linux/amd64 public.ecr.aws/lambda/python:3.13

# chrome 실행에 필요한 패키지 설치
COPY chrome-requirements.txt ./
RUN dnf install -y $(cat chrome-requirements.txt)

# python에서 크롤링을 실행하기 위한 파이썬 패키지 설치
RUN pip install selenium webdriver_manager pymysql sqlalchemy

COPY --from=build /opt/chrome-linux64 /opt/chrome
COPY --from=build /opt/chromedriver-linux64 /opt/

COPY .env /home/.env

# competitions 실행

COPY . ./
CMD [ "src.competitions.lambda_handler" ]