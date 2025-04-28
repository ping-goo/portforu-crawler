FROM openjdk:17-jdk-slim AS builder

WORKDIR /build

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget gnupg ca-certificates curl unzip \
 && mkdir -p /etc/apt/keyrings \
 && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
 && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends google-chrome-stable \
 \
 && wget -qO /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/135.0.4160.0/linux64/chromedriver-linux64.zip \
 && unzip /tmp/chromedriver.zip -d /tmp/ \
 && mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /tmp/* /var/lib/apt/lists/*

# 실제 실행용
FROM openjdk:17-jdk-slim

# xvfb 설치
RUN apt-get update \
 && apt-get install -y --no-install-recommends xvfb \
 && rm -rf /var/lib/apt/lists/*

# Chrome, Chromedriver 복사
COPY --from=builder /usr/bin/google-chrome-stable /usr/bin/
COPY --from=builder /usr/local/bin/chromedriver /usr/local/bin/

WORKDIR /app

# 앱 jar 복사
COPY build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar app.jar

# 엔트리포인트 설정
ENTRYPOINT ["xvfb-run","-a","--server-args=-screen 0 1920x1080x24","java","-Dspring.profiles.active=prod","-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage","-jar","app.jar"]
