package pinggu.portforu_crawler.jobkorea.service

import org.jsoup.Jsoup
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import pinggu.portforu_crawler.common.domain.JobPosting
import pinggu.portforu_crawler.common.domain.JobPostingRepository
import pinggu.portforu_crawler.config.UrlBloomFilterService
import kotlin.random.Random

@Component
class JkJobEntryProcessor(
    private val detailParser: JkDetailParser,
    private val jkTagService: JkTagService,
    private val jobPostingRepository: JobPostingRepository,
    private val urlBloomFilter: UrlBloomFilterService
) {
    private val logger = LoggerFactory.getLogger(JkJobEntryProcessor::class.java)

    fun processJobEntry(element: WebElement): JobPosting? {
        val title = element.text.trim()
        val link = element.getAttribute("href").trim()

        if (!urlBloomFilter.isNewUrl(link)) {
            logger.debug("이미 처리된 링크(Bloom), 스킵: {}", link)
            return null
        }

        Thread.sleep(Random.nextLong(2000, 5000))
        val detailHtml = Jsoup.connect(link).timeout(10_000).get().html()
        val detailData = detailParser.parseDetail(detailHtml) ?: return null

        val parsedSkills = detailData.skills
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            .joinToString(", ").ifBlank { "-1" }

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

        if (jobPostingRepository.findByLink(link) == null) {
            try {
                jobPostingRepository.save(jobPosting)
                jkTagService.saveTags(detailData.skills.split(","), jobPosting)
                logger.info("Saved JobKorea entry: {}", jobPosting.title)
            } catch (e: DataIntegrityViolationException) {
                logger.warn("중복 링크로 저장 실패: {}", link)
            }
        }

        return jobPosting
    }
}

