FROM openjdk:17-jdk-slim

RUN apt-get update && \
    apt-get install -y \
      wget \
      gnupg2 \
      ca-certificates \
      apt-transport-https \
      unzip && \
    rm -rf /var/lib/apt/lists/*

RUN wget -qO- https://dl-ssl.google.com/linux/linux_signing_key.pub \
     | gpg --dearmor --yes \
       > /usr/share/keyrings/google-linux-signing-keyring.gpg && \
    echo \
      "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-signing-keyring.gpg] \
       http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list

RUN apt-get update && \
    apt-get install -y google-chrome-stable && \
    CHROME_VERSION=$(google-chrome --product-version) && \
    wget -qO /tmp/chromedriver_linux64.zip \
      "https://chromedriver.storage.googleapis.com/${CHROME_VERSION}/chromedriver_linux64.zip" && \
    unzip /tmp/chromedriver_linux64.zip -d /usr/local/bin && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf /tmp/chromedriver_linux64.zip /var/lib/apt/lists/*

WORKDIR /app
ARG JAR_FILE=build/libs/portforu-crawler-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["xvfb-run","-a","--server-args=-screen 0 1920x1080x24",\
            "java","-Dspring.profiles.active=prod",\
            "-Dchrome.options.args=--no-sandbox,--disable-dev-shm-usage",\
            "-jar","app.jar"]
