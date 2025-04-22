package pinggu.portforu_crawler.saramin.service

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.domain.JobEntry
import pinggu.portforu_crawler.common.domain.JobEntryRepository
import pinggu.portforu_crawler.saramin.SaraminScroller
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

@Component
class SaraminPageProcessor(
    private val jobEntryRepository: JobEntryRepository,
    private val detailParser: SaraminDetailParser
) {
    private val log = LoggerFactory.getLogger(SaraminPageProcessor::class.java)

    private val defaultDate: ZonedDateTime = ZonedDateTime.of(
        LocalDate.of(1970, 1, 1).atStartOfDay(), ZoneId.of("Asia/Seoul")
    )

    fun fetchEntries(pageNum: Int, baseUrl: String, driver: WebDriver): List<JobEntry> {
        val results = mutableListOf<JobEntry>()
        driver.get("$baseUrl$pageNum")
        SaraminScroller.scrollToBottom(driver)  // 목록 첫 진입 시 스크롤 :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}
        log.info("Loading page: $baseUrl$pageNum")

        val links = driver.findElements(By.cssSelector(".job_tit"))
            .mapNotNull { section ->
                if (section.findElements(By.cssSelector(".similar_recruit")).isNotEmpty()) null
                else section.findElement(By.cssSelector("a.str_tit")).getAttribute("href")
            }

        for ((idx, link) in links.withIndex()) {
            if (jobEntryRepository.findByLink(link) != null) continue

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

            val title = driver.findElement(By.cssSelector("h1.tit_job")).text.trim()
            val rawSkills = ""
            val entry = JobEntry(
                title = title,
                company = data.company,
                location = data.location,
                link = link,
                salary = data.salary.toString(),
                duty = "개발자",
                employmentType = data.employmentType,
                educationLevel = data.educationLevel,
                experienceYears = "-1",
                keyAbilities = "-1",
                hiringStartAt = data.hiringStartAt ?: defaultDate,
                hiringEndAt = data.hiringEndAt ?: defaultDate,
                skills = rawSkills.ifBlank { "-1" },
                minExperienceYears = data.minExperienceYears ?: -1,
                maxExperienceYears = data.maxExperienceYears ?: -1
            )

            try {
                jobEntryRepository.save(entry)
                results += entry
                log.info("Saved Saramin entry #${idx + 1}: ${entry.title}")
            } catch (e: DataIntegrityViolationException) {
                log.warn("중복 링크로 저장 실패: {}", link)
            }

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

