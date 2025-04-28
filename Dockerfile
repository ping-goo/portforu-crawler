FROM openjdk:17-jdk-slim AS builder

WORKDIR /build

RUN apt-get update && apt-get install -y --no-install-recommends \
      wget gnupg2 ca-certificates curl unzip \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
         | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] \
         http://dl.google.com/linux/chrome/deb/ stable main" \
         > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    && CHROME_MAJOR=$(google-chrome --product-version | cut -d. -f1) \
    && CHROMEDRIVER_VER=$(wget -qO- \
         "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_MAJOR}") \
    && wget -qO /tmp/chromedriver_linux64.zip \
         "https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VER}/chromedriver_linux64.zip" \
    && unzip /tmp/chromedriver_linux64.zip -d /usr/local/bin \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf /tmp/* /var/lib/apt/lists/* \

FROM openjdk:17-jdk-slim

RUN apt-get update \
 && apt-get install -y --no-install-recommends xvfb \
 && rm -rf /var/lib/apt/lists/*

COPY --from=builder /usr/bin/google-chrome-stable /usr/bin/google-chrome-stable
COPY --from=builder /usr/local/bin/chromedriver      /usr/local/bin/chromedriver

WORKDIR /app
ARG JAR_FILE=build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT [
  "xvfb-run", "-a", "--server-args=-screen 0 1920x1080x24",
  "java",
    "-Dspring.profiles.active=prod",
    "-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage",
    "-jar", "app.jar"
]
