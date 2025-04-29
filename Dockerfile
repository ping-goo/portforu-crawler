# 1) Builder 단계: Chromedriver만 다운로드
FROM openjdk:17-jdk-slim AS builder
SHELL ["/bin/bash","-euxo","pipefail","-c"]

WORKDIR /build
RUN apt-get update \
 && apt-get install -y --no-install-recommends wget unzip ca-certificates \
 && wget -qO /tmp/chromedriver.zip \
      https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.114/linux64/chromedriver-linux64.zip \
 && unzip /tmp/chromedriver.zip -d /tmp/ \
 && mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/ \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /var/lib/apt/lists/* /tmp/*

# 2) Runtime 단계: Java + Chrome + System Library + Chromedriver
FROM openjdk:17-jdk-slim
SHELL ["/bin/bash","-euxo","pipefail","-c"]

# Chrome APT repository 설정
RUN apt-get update \
 && apt-get install -y --no-install-recommends wget gnupg ca-certificates curl \
 && mkdir -p /etc/apt/keyrings \
 && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
      | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
 && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update

# Google Chrome 및 필요한 시스템 라이브러리 설치
RUN apt-get install -y --no-install-recommends \
      google-chrome-stable \
      xvfb xauth \
      libglib2.0-0 libnss3 libx11-xcb1 libxcomposite1 libxcursor1 \
      libxdamage1 libxrandr2 libatk1.0-0 libatk-bridge2.0-0 \
      libgtk-3-0 libgbm1 libasound2 libcups2 libdrm2 \
 && rm -rf /var/lib/apt/lists/*

# Builder 단계에서 가져온 Chromedriver 복사
COPY --from=builder /usr/local/bin/chromedriver /usr/local/bin/

# 애플리케이션 JAR 배포
WORKDIR /app
COPY build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar app.jar

# Headless 옵션 포함 ENTRYPOINT (한 줄 작성)
ENTRYPOINT ["java", "-Dchrome.options.args=--headless,--no-sandbox,--disable-dev-shm-usage,--disable-gpu", "-jar", "app.jar"]
