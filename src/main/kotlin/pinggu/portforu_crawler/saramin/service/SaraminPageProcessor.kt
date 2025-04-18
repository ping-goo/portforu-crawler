package pinggu.portforu_crawler.saramin.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.saramin.SaraminScroller
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntry
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntryRepository
import java.time.Duration
import kotlin.random.Random

@Component
class SaraminPageProcessor(
    private val entryRepo: SaraminJobEntryRepository,
    private val detailParser: SaraminDetailParser
) {
    private val log = LoggerFactory.getLogger(SaraminPageProcessor::class.java)

    fun fetchEntries(pageNum: Int, baseUrl: String, driver: WebDriver): List<SaraminJobEntry> {
        val results = mutableListOf<SaraminJobEntry>()
        driver.get("$baseUrl$pageNum")
        SaraminScroller.scrollToBottom(driver)  // 목록 첫 진입 시 스크롤 :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}
        log.info("Loading page: $baseUrl$pageNum")

        val links = driver.findElements(By.cssSelector(".job_tit"))
            .mapNotNull { section ->
                if (section.findElements(By.cssSelector(".similar_recruit")).isNotEmpty()) null
                else section.findElement(By.cssSelector("a.str_tit")).getAttribute("href")
            }

        for ((idx, link) in links.withIndex()) {
            if (entryRepo.existsByLink(link)) continue

            Thread.sleep(Random.nextLong(200, 1000))
            driver.get(link)

            // 상세 페이지 파싱
            val data = detailParser.parseDetail(driver)
            if (data == null) {
                // 상세 파싱 실패 시에도 목록으로 복귀 후 대기
                driver.navigate().back()

                // ◀ 변경: 목록 페이지 요소 로드 대기
                WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job_tit")))

                continue
            }

            // 엔티티 생성 및 저장
            val entry = SaraminJobEntry.toEntity(
                title              = driver.findElement(By.cssSelector("h1.tit_job")).text.trim(),
                company            = data.company,
                location           = data.location,
                salary             = data.salary,
                employmentType     = data.employmentType,
                educationLevel     = data.educationLevel,
                link               = link,
                minExperienceYears = data.minExperienceYears,
                maxExperienceYears = data.maxExperienceYears,
                duty               = "개발자",
                hiringStartAt      = data.hiringStartAt,
                hiringEndAt        = data.hiringEndAt
            )
            entryRepo.save(entry)
            results += entry
            log.info("Processed #${idx + 1}: ${entry.title}")

            // 목록 페이지로 복귀
            driver.navigate().back()

            // ◀ 변경: 목록 페이지 요소 로드 대기
            WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job_tit")))

            // ◀ 변경: 제대로 로드된 목록에 대해서만 스크롤
            SaraminScroller.scrollToBottom(driver)
        }

        return results
    }
}

