FROM --platform=linux/amd64 public.ecr.aws/lambda/python:3.11 AS build

# Chrome 및 Chromedriver 설치
RUN yum install -y unzip && \
    curl -Lo /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/97.0.4692.71/chromedriver_linux64.zip && \
    curl -Lo /tmp/chrome-linux.zip https://www.googleapis.com/download/storage/v1/b/chromium-browser-snapshots/o/Linux_x64%2F938549%2Fchrome-linux.zip?alt=media && \
    unzip /tmp/chromedriver.zip -d /opt/ && \
    unzip /tmp/chrome-linux.zip -d /opt/

FROM --platform=linux/amd64 public.ecr.aws/lambda/python:3.11

# Chrome 실행에 필요한 패키지
COPY chrome-requirements.txt ./
RUN yum install -y $(cat chrome-requirements.txt)

# Python 패키지 설치
COPY requirements.txt ./
RUN pip install --upgrade pip && pip install -r requirements.txt

# Chrome, Chromedriver 복사
COPY --from=build /opt/chrome-linux /opt/chrome
COPY --from=build /opt/chromedriver /usr/bin/chromedriver
RUN chmod +x /usr/bin/chromedriver

# 코드 복사
COPY *.py /var/task

WORKDIR /var/task

CMD [ "lambda_function.lambda_handler" ]