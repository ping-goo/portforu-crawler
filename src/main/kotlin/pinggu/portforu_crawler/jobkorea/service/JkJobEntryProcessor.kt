package pinggu.portforu_crawler.jobkorea.service

import org.jsoup.Jsoup
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import pinggu.portforu_crawler.common.domain.JobPosting
import pinggu.portforu_crawler.common.domain.JobPostingRepository
import pinggu.portforu_crawler.config.UrlBloomFilterService
import pinggu.portforu_crawler.common.util.SlackNotifier
import pinggu.portforu_crawler.stats.CrawlerStats
import pinggu.portforu_crawler.common.util.SkillNormalizer
import pinggu.portforu_crawler.messaging.dto.JobCloseEvent
import pinggu.portforu_crawler.messaging.producer.JobCloseEventPublisher
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@Component
class JkJobEntryProcessor(
    private val detailParser: JkDetailParser,
    private val jkTagService: JkTagService,
    private val jobPostingRepository: JobPostingRepository,
    private val urlBloomFilter: UrlBloomFilterService,  
    private val slackNotifier: SlackNotifier,          
    private val crawlerStats: CrawlerStats,
    private val jobCloseEventPublisher: JobCloseEventPublisher
) {
    private val logger = LoggerFactory.getLogger(JkJobEntryProcessor::class.java)

    fun processJobEntry(element: WebElement): JobPosting? {
        val title = element.text.trim()
        val link = element.getAttribute("href").trim()

        // Bloom Filter로 중복 URL 확인
        if (!urlBloomFilter.isNewUrl(link)) {
            logger.debug("이미 처리된 링크(Bloom), 스킵: {}", link)
            return null
        }

        // 상세 페이지 요청 전 2초~5초 사이의 랜덤 딜레이 추가
        Thread.sleep(Random.nextLong(2000, 5000))

        val detailHtml = Jsoup.connect(link)
            .timeout(10000)
            .get()
            .html()
        logger.debug("Fetched detail page HTML for job [{}]", link)

        // 파서로 상세 페이지 데이터 추출
        val detailData = detailParser.parseDetail(detailHtml) ?: run {
            crawlerStats.parseFailCount.incrementAndGet()
            slackNotifier.send("JobKorea 파싱 실패: $link")
            logger.warn("Failed to parse detail for link: {}", link)
            return null
        }

        val tags = SkillNormalizer.normalize(detailData.skills)
        val parsedSkills = tags.joinToString(", ").ifBlank { "-1" }

        val jobPosting = JobPosting(
            title = title,
            company = detailData.company,
            location = detailData.location,
            link = link,
            salary = detailData.salary,
            duty = "개발자",
            employmentType = detailData.employmentType,
            educationLevel = detailData.educationLevel,
            experienceYears = detailData.experience,
            keyAbilities = detailData.keyAbilities,
            minExperienceYears = -1,
            maxExperienceYears = -1,
            hiringStartAt = detailData.hiringStartAt,
            hiringEndAt = detailData.hiringEndAt,
            skills = parsedSkills
        )

        // 중복 링크가 아니면 DB에 저장
        if (jobPostingRepository.findByLink(link) == null) {
            try {
                jobPostingRepository.save(jobPosting)
                jkTagService.saveTags(tags, jobPosting)
                crawlerStats.successCount.incrementAndGet()
                logger.info("Saved JobKorea entry: {}", jobPosting.title)

                // Delay 또는 즉시 발송 처리
                jobPosting.hiringEndAt?.let { endAt ->
                    val now = ZonedDateTime.now()
                    val delayMillis = ChronoUnit.MILLIS.between(now, endAt.minusDays(1))
                    val event = JobCloseEvent(jobPosting.id!!, jobPosting.title)

                    if (delayMillis > 0) {
                        jobCloseEventPublisher.publishWithDelay(event, delayMillis)
                        logger.info("Delay 예약 발송: {} ({}ms)", jobPosting.title, delayMillis)
                    } else {
                        jobCloseEventPublisher.publishImmediately(event)
                        logger.info("마감 임박 → 즉시 발송: {}", jobPosting.title)
                    }
                }

            } catch (e: DataIntegrityViolationException) {
                crawlerStats.saveFailCount.incrementAndGet()
                logger.warn("중복 링크로 저장 실패: {}", link)
            }
        }

        return jobPosting
    }
}
