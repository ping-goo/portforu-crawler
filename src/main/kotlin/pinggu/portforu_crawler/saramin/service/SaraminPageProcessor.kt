package pinggu.portforu_crawler.saramin.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.domain.JobPosting
import pinggu.portforu_crawler.common.domain.JobPostingRepository
import pinggu.portforu_crawler.common.util.orDefault
import pinggu.portforu_crawler.config.UrlBloomFilterService
import pinggu.portforu_crawler.saramin.SaraminScroller
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random
import pinggu.portforu_crawler.common.util.SlackNotifier
import pinggu.portforu_crawler.stats.CrawlerStats

@Component
class SaraminPageProcessor(
    private val jobPostingRepository: JobPostingRepository,
    private val detailParser: SaraminDetailParser,
    private val crawlerStats: CrawlerStats,
    private val slackNotifier: SlackNotifier,
    private val urlBloomFilter: UrlBloomFilterService
) {
    private val log = LoggerFactory.getLogger(SaraminPageProcessor::class.java)

    private val defaultDate: ZonedDateTime = ZonedDateTime.of(
        LocalDate.of(1970, 1, 1).atStartOfDay(), ZoneId.of("Asia/Seoul")
    )

    fun fetchEntries(pageNum: Int, baseUrl: String, driver: WebDriver): List<JobPosting> {
        val results = mutableListOf<JobPosting>()
        driver.get("$baseUrl$pageNum")

        SaraminScroller.scrollToBottom(driver)  // 목록 첫 진입 시 스크롤

        log.info("Loading page: $baseUrl$pageNum")

        val links = driver.findElements(By.cssSelector(".job_tit"))
            .mapNotNull { section ->
                if (section.findElements(By.cssSelector(".similar_recruit")).isNotEmpty()) null
                else section.findElement(By.cssSelector("a.str_tit")).getAttribute("href")
            }

        for ((idx, link) in links.withIndex()) {

            if (!urlBloomFilter.isNewUrl(link)) {
                log.debug("이미 처리된 링크(Bloom), 스킵: {}", link)
                continue
            }

            if (jobPostingRepository.findByLink(link) != null) continue

            Thread.sleep(Random.nextLong(200, 1000))
            driver.get(link)

            val data = detailParser.parseDetail(driver)
            if (data == null) {
                crawlerStats.parseFailCount.incrementAndGet()
                slackNotifier.send("Saramin 파싱 실패: $link")

                driver.navigate().back()
                WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job_tit")))
                continue
            }

            val title = driver.findElement(By.cssSelector("h1.tit_job")).text.trim()
            val rawSkills = ""
            val entry = JobPosting(
                title = title,
                company = data.company.orDefault("-1"),
                location = data.location.orDefault("-1"),
                link = link,
                salary = data.salary.orDefault("-1"),
                duty = "개발자",
                employmentType = data.employmentType.orDefault("-1"),
                educationLevel = data.educationLevel.orDefault("-1"),
                experienceYears = "-1",
                keyAbilities = "-1",
                hiringStartAt = data.hiringStartAt.orDefault(defaultDate),
                hiringEndAt = data.hiringEndAt.orDefault(defaultDate),
                skills = rawSkills.orDefault("-1"),
                minExperienceYears = data.minExperienceYears.orDefault(-1),
                maxExperienceYears = data.maxExperienceYears.orDefault(-1)
            )

            try {
                jobPostingRepository.save(entry)
                crawlerStats.successCount.incrementAndGet()
                results += entry
                log.info("Saved Saramin entry #${idx + 1}: ${entry.title}")
            } catch (e: DataIntegrityViolationException) {
                crawlerStats.saveFailCount.incrementAndGet()
                log.warn("중복 링크로 저장 실패: {}", link)
            }

            driver.navigate().back()
            WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job_tit")))
            SaraminScroller.scrollToBottom(driver)
        }

        return results
    }
}
