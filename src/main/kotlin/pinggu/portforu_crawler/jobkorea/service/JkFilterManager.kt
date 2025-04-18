package pinggu.portforu_crawler.jobkorea.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component


@Component
class JkFilterManager {

    fun applyFilters(wait: WebDriverWait, driver: WebDriver) {
        try {
            // 1. "직무" 필터 버튼 클릭
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("p.btn_tit")))
                .apply { click() }
                .also { Thread.sleep(3000) }

            // 2. 대분류 "AI·개발·데이터" 선택
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("label[for='duty_step1_10031']")))
                .also { element ->
                    wait.until(ExpectedConditions.elementToBeClickable(element)).click()
                }
                .also { Thread.sleep(3000) }

            // 3. 중분류 목록 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("duty_step2_10031_ly")))

            // 4. 세부 직무 선택: 백엔드, 프론트엔드, 웹, 앱, 시스템, 네트워크, 소프트웨어개발자
            val subCodes = listOf("1000229", "1000230", "1000231", "1000232", "1000233", "1000234", "1000239")
            subCodes.forEach { code ->
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("label[for='duty_step2_$code']")))
                    .also { wait.until(ExpectedConditions.elementToBeClickable(it)).click() }
                Thread.sleep(1000)
            }

            // 5. 검색 버튼 클릭
            wait.until(ExpectedConditions.elementToBeClickable(By.id("dev-btn-search")))
                .apply { click() }
                .also { Thread.sleep(3000) }

            // 6. 검색 결과가 로딩될 때까지 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("strong a.link.normalLog")))

            // 7. 최신 업데이트 정렬 적용
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("orderTab")))
                .let { Select(it) }
                .apply { selectByValue("3") }
                .also { Thread.sleep(3000) }

            // 8. 변경된 결과 로딩 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("strong a.link.normalLog")))
        } catch (e: Exception) {
            println("JcFilterManager.applyFilters 오류: ${e.message}")
        }
    }
}