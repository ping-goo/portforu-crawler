package pinggu.portforu_crawler.jobkorea.service

import org.jsoup.Jsoup
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import pinggu.portforu_crawler.common.domain.JobEntry
import pinggu.portforu_crawler.common.domain.JobEntryRepository
import pinggu.portforu_crawler.common.util.orDefault
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class JkJobEntryProcessor(
    private val detailParser: JkDetailParser,
    private val jkTagService: JkTagService,
    private val jobEntryRepository: JobEntryRepository
) {
    private val logger = LoggerFactory.getLogger(JkJobEntryProcessor::class.java)

    fun processJobEntry(element: WebElement): JobEntry? {
        return try {
            // 목록 페이지에서 기본 정보 추출 (제목과 상세 페이지 링크)
            val title = element.text.trim()
            val link = element.getAttribute("href").trim()
            logger.info("Fetching detail page: {}", link)

            // 상세 페이지 요청 전 2초~5초 사이의 랜덤 딜레이 추가
            Thread.sleep(kotlin.random.Random.nextLong(2000, 5000))

            val detailHtml = Jsoup.connect(link)
                .timeout(10000)
                .get()
                .html()
            logger.debug("Fetched detail page HTML for job [{}]", link)

            // 파서로 상세 페이지 데이터 추출
            val detailData = detailParser.parseDetail(detailHtml)
            if (detailData == null) {
                logger.warn("Failed to parse detail for link: {}", link)
                return null
            }

            // 스킬 문자열 파싱
            val parsedSkills = detailData.skills
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString(", ")
                .ifBlank { "-1" }

            // 엔티티 생성 시 모든 필요한 필드를 detailData의 값으로 할당
            val jobEntry = JobEntry(
                title = title,
                company = detailData.company.orDefault("-1"),
                location = detailData.location.orDefault("-1"),
                link = link,
                salary = detailData.salary.orDefault("-1"),
                duty = "개발자",
                employmentType = detailData.employmentType.orDefault("-1"),
                educationLevel = detailData.educationLevel.orDefault("-1"),
                experienceYears = detailData.experience.orDefault("-1"),
                keyAbilities = detailData.keyAbilities.orDefault("-1"),
                hiringStartAt = detailData.hiringStartAt.orDefault(),
                hiringEndAt = detailData.hiringEndAt.orDefault(),
                skills = parsedSkills,
                minExperienceYears = -1,
                maxExperienceYears = -1
            )
           
            if (jobEntryRepository.findByLink(link) == null) {
                try {
                    jobEntryRepository.save(jobEntry)

                    // 저장 성공한 경우에만 태그 저장
                    val tags = detailData.skills
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    if (tags.isNotEmpty()) {
                        jkTagService.saveTags(tags, jobEntry)
                    }

                } catch (e: DataIntegrityViolationException) {
                    logger.warn("중복 링크로 저장 실패: {}", link)
                }
            }

            return jobEntry
        } catch (e: Exception) {
            logger.error("Job entry 처리 오류: {}", e.message)
            null
        }
    }
}
