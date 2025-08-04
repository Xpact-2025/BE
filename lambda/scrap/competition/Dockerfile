FROM public.ecr.aws/lambda/python:3.13 AS build

# install unzip, install chrome & webdriver -> /opt에 위치하도록
RUN dnf install -y unzip && \
    curl -Lo "/tmp/chromedriver-linux64.zip" "https://storage.googleapis.com/chrome-for-testing-public/138.0.7204.183/linux64/chromedriver-linux64.zip" && \
    curl -Lo "/tmp/chrome-linux64.zip" "https://storage.googleapis.com/chrome-for-testing-public/138.0.7204.183/linux64/chrome-linux64.zip" && \
    unzip /tmp/chromedriver-linux64.zip -d /opt/ && \
    unzip /tmp/chrome-linux64.zip -d /opt/

FROM public.ecr.aws/lambda/python:3.13

# chrome 실행에 필요한 패키지 설치
RUN dnf install -y atk cups-libs gtk3 libXcomposite alsa-lib libXcursor libXdamage libXext libXi libXrandr libXScrnSaver libXtst pango at-spi2-atk libXt xorg-x11-server-Xvfb xorg-x11-xauth dbus-glib dbus-glib-devel nss mesa-libgbm

# python에서 크롤링을 실행하기 위한 파이썬 패키지 설치
RUN pip install selenium==4.32.0

COPY --from=build /opt/chrome-linux64 /opt/chrome
COPY --from=build /opt/chromedriver-linux64/chromedriver /opt/chromedriver

RUN chmod +x /opt/chromedriver
RUN chmod +x /opt/chrome/chrome

# main 실행
ENV S3_BUCKET=xpactbucket
COPY main.py ./
COPY crawling_competition.py ./
CMD [ "main.lambda_handler" ]