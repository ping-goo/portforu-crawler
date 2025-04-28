# Builder 단계
FROM openjdk:17-jdk-slim AS builder
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

WORKDIR /build

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget gnupg ca-certificates curl unzip \
 && mkdir -p /etc/apt/keyrings \
 \
 # Google Chrome 설치
 && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
    | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
 && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends google-chrome-stable \
 \
 # Chromedriver 다운로드 및 설치 (Chrome 135.x 용)
 && wget -q -O /tmp/chromedriver.zip \
      https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/135.0.4160.0/linux64/chromedriver-linux64.zip \
 \
 # 압축 풀고 파일 유무 체크
 && unzip /tmp/chromedriver.zip -d /tmp/ \
 && if [ ! -f /tmp/chromedriver-linux64/chromedriver ]; then \
      echo "Chromedriver not found after unzip!" && exit 1; \
    fi \
 \
 # 바이너리 이동 및 정리
 && mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/ \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /tmp/* /var/lib/apt/lists/*

# 실제 실행용 이미지
FROM openjdk:17-jdk-slim
SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

# Xvfb 설치
RUN apt-get update \
 && apt-get install -y --no-install-recommends xvfb \
 && rm -rf /var/lib/apt/lists/*

# Chrome & Chromedriver 복사
COPY --from=builder /usr/bin/google-chrome-stable /usr/bin/
COPY --from=builder /usr/local/bin/chromedriver /usr/local/bin/

WORKDIR /app

# 애플리케이션 JAR 복사
COPY build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 시작 커맨드
ENTRYPOINT ["xvfb-run","-a","--server-args=-screen 0 1920x1080x24","java","-Dspring.profiles.active=prod","-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage","-jar","app.jar"]
