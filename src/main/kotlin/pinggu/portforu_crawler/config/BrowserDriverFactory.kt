package pinggu.portforu_crawler.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Component


// Selenium WebDriver 인스턴스를 생성해주는 팩토리 클래스
// 각 크롤러가 웹 크롤링을 수행할 때 동일한 설정의 브라우저 인스턴스를 생성
@Component
class BrowserDriverFactory {
    fun createDriver(): WebDriver {
        // 컨테이너에 복사된 Chromedriver 경로로 설정
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")

        val options = ChromeOptions().apply {
            // Headless 모드 활성화
            addArguments("--headless")
            addArguments("--disable-gpu")

            // 기타 필요한 옵션
            addArguments("--start-maximized")
            addArguments("--window-size=1920,1080")
            addArguments("--remote-allow-origins=*")
            addArguments("--disable-dev-shm-usage", "--no-sandbox")
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("--disable-features=NetworkService")
            addArguments("--remote-debugging-port=${(9222..9299).random()}")

        }
        return ChromeDriver(options)
    }
}
