package pinggu.portforu_crawler.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Component
import java.util.*


// Selenium WebDriver 인스턴스를 생성해주는 팩토리 클래스
// 각 크롤러가 웹 크롤링을 수행할 때 동일한 설정의 브라우저 인스턴스를 생성
@Component
class BrowserDriverFactory {

    fun createDriver(): WebDriver {
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver")

        val options = ChromeOptions()
        options.addArguments("--start-maximized") //브라우저 전체 화면 표시
        options.addArguments("--disable-gpu", "--window-size=1920,1080")
        options.addArguments("--remote-allow-origins=*")  //웹소켓 차단 방지
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox")  //리소스 제한 해결
        options.addArguments("--disable-blink-features=AutomationControlled")  //봇 탐지 우회
        options.addArguments("--disable-features=NetworkService")  //웹소켓 연결 문제 해결

        // 포트 번호를 랜덤으로 지정하여 원격 디버깅 등을 지원
        options.addArguments("--remote-debugging-port=${(9222..9299).random()}")

        // 임시 프로필 사용
        options.addArguments("--user-data-dir=/tmp/chrome-profile-${UUID.randomUUID()}")

        WebDriverManager.chromedriver().setup()
        return ChromeDriver(options)
    }
}