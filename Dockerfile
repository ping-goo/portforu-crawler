FROM openjdk:17-jdk-slim

RUN apt-get update \
 && apt-get install -y \
      xvfb \
      wget gnupg ca-certificates fonts-liberation unzip \
 && rm -rf /var/lib/apt/lists/*

# Chrome & ChromeDriver 설치
RUN wget -qO- https://dl-ssl.google.com/linux/linux_signing_key.pub \
      | apt-key add - \
 && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update \
 && apt-get install -y google-chrome-stable \
 && CHROME_VERSION=$(google-chrome --version | awk '{print $3}') \
 && wget -qO /tmp/chromedriver_linux64.zip \
      "https://chromedriver.storage.googleapis.com/${CHROME_VERSION}/chromedriver_linux64.zip" \
 && unzip /tmp/chromedriver_linux64.zip -d /usr/local/bin \
 && chmod +x /usr/local/bin/chromedriver \
 && rm -rf /tmp/chromedriver_linux64.zip /var/lib/apt/lists/*

# 애플리케이션 복사
WORKDIR /app
ARG JAR_FILE=build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["xvfb-run", "-a", "--server-args=-screen 0 1920x1080x24", \
             "java", \
               "-Dspring.profiles.active=prod", \
               "-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage", \
               "-jar", "app.jar"]