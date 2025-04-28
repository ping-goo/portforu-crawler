FROM openjdk:17-jdk-slim as builder

WORKDIR /build

# Chrome과 ChromeDriver 설치를 위한 필수 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip ca-certificates curl gnupg2 fonts-liberation \
 && rm -rf /var/lib/apt/lists/*

# Chrome 설치 (.deb 다운로드 직접 설치)
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
 && apt-get update \
 && apt-get install -y ./google-chrome-stable_current_amd64.deb \
 && rm google-chrome-stable_current_amd64.deb

# ChromeDriver 설치
RUN CHROME_VERSION=$(google-chrome --version | awk '{print $3}') \
 && CHROMEDRIVER_VERSION=$(wget -qO- https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION%%.*}) \
 && wget -qO /tmp/chromedriver_linux64.zip "https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip" \
 && unzip /tmp/chromedriver_linux64.zip -d /usr/local/bin \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /tmp/* /var/lib/apt/lists/*

# 런타임 스테이지
FROM openjdk:17-jdk-slim

# xvfb만 설치
RUN apt-get update && apt-get install -y --no-install-recommends xvfb && rm -rf /var/lib/apt/lists/*

# 필요한 바이너리만 복사
COPY --from=builder /usr/bin/google-chrome-stable /usr/bin/
COPY --from=builder /usr/local/bin/chromedriver /usr/local/bin/

# 애플리케이션 복사
WORKDIR /app
COPY build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar app.jar

# 실행
ENTRYPOINT ["xvfb-run", "-a", "--server-args=-screen 0 1920x1080x24", "java", "-Dspring.profiles.active=prod", "-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage", "-jar", "app.jar"]