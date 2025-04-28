FROM openjdk:17-jdk-slim AS builder
SHELL ["/bin/bash","-euxo","pipefail","-c"]
WORKDIR /build

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget gnupg ca-certificates curl unzip \
 && mkdir -p /etc/apt/keyrings \
 && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
 && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends google-chrome-stable \
 && wget -qO /tmp/chromedriver.zip https://storage.googleapis.com/chrome-for-testing-public/135.0.7049.114/linux64/chromedriver-linux64.zip \
 && unzip /tmp/chromedriver.zip -d /tmp/ \
 && if [ ! -f /tmp/chromedriver-linux64/chromedriver ]; then echo "Chromedriver not found after unzip!" >&2; exit 1; fi \
 && mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/ \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /tmp/* /var/lib/apt/lists/*

FROM openjdk:17-jdk-slim
SHELL ["/bin/bash","-euxo","pipefail","-c"]

RUN apt-get update \
 && apt-get install -y --no-install-recommends xvfb xauth \
 && rm -rf /var/lib/apt/lists/*

COPY --from=builder /usr/bin/google-chrome-stable /usr/bin/
COPY --from=builder /usr/local/bin/chromedriver /usr/local/bin/

WORKDIR /app
COPY build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["xvfb-run","-a","--server-args=-screen 0 1920x1080x24","java","-Dspring.profiles.active=prod","-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage","-jar","app.jar"]
