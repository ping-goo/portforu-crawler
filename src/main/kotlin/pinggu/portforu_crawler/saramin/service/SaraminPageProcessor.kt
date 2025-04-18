package pinggu.portforu_crawler.saramin.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.saramin.SaraminScroller
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntry
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntryRepository
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
        SaraminScroller.scrollToBottom(driver)  // 목록 페이지만 스크롤
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

            // 상세 페이지: **스크롤·대기 제거** → 즉시 파싱
            val data = detailParser.parseDetail(driver)
            if (data == null) {
                driver.navigate().back()
                continue
            }

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

            driver.navigate().back()              // 목록 복귀
            SaraminScroller.scrollToBottom(driver)     // 목록 페이지 유지
        }

        return results
    }
}
