package pinggu.portforu_crawler.saramin.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.saramin.dto.SaraminJobDetailData
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class SaraminDetailParser {
    private val log: Logger = LoggerFactory.getLogger(SaraminDetailParser::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
    private val zoneId = ZoneId.of("Asia/Seoul")

    fun parseDetail(driver: WebDriver): SaraminJobDetailData? {
        log.info("Parsing job details")
        return try {
            // 회사명
            val companyEl = WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.company")))
            val company = companyEl.getAttribute("title").trim()

            // 경력
            val careerText = driver.findElement(
                By.xpath("//dt[text()='경력']/following-sibling::dd//strong")
            ).text
            val nums = Regex("\\d+").findAll(careerText).map { it.value.toInt() }.toList()
            val minExp = nums.getOrNull(0) ?: 0
            val maxExp = nums.getOrNull(1) ?: (minExp + 3)

            // 학력
            val educationLevel = driver.findElement(
                By.xpath("//dt[text()='학력']/following-sibling::dd//strong")
            ).text.trim()

            // 근무형태
            val employmentType = driver.findElement(
                By.xpath("//dt[text()='근무형태']/following-sibling::dd//strong")
            ).text.trim()

            // 급여
            val salaryText = driver.findElement(
                By.xpath("//dt[text()='급여']/following-sibling::dd")
            ).getAttribute("innerText").trim()
            // 공백만 있는 경우에만 "-1", 나머지는 글자 그대로 저장
            val salary = if (salaryText.isBlank()) "-1" else salaryText

            // 근무지역
            val location = driver.findElement(
                By.xpath("//dt[text()='근무지역']/following-sibling::dd")
            ).text.trim()

            // 시작일·마감일
            val hiringStartAt = runCatching {
                val t = driver.findElement(By.xpath("//dt[text()='시작일']/following-sibling::dd")).text
                LocalDateTime.parse(t, dateFormatter).atZone(zoneId)
            }.getOrNull()
            val hiringEndAt = runCatching {
                val t = driver.findElement(By.xpath("//dt[text()='마감일']/following-sibling::dd")).text
                LocalDateTime.parse(t, dateFormatter).atZone(zoneId)
            }.getOrNull()

            SaraminJobDetailData(
                company           = company,
                location          = location,
                employmentType    = employmentType,
                educationLevel    = educationLevel,
                salary            = salary,
                minExperienceYears= minExp,
                maxExperienceYears= maxExp,
                hiringStartAt     = hiringStartAt,
                hiringEndAt       = hiringEndAt
            )
        } catch (e: Exception) {
            log.error("상세 파싱 실패: ${e.message}", e)
            null
        }
    }
}

