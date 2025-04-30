package pinggu.portforu_crawler.saramin.service

import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pinggu.portforu_crawler.common.domain.JobPosting
import pinggu.portforu_crawler.config.BrowserDriverFactory

@Service
class SaraminCrawlerService(
    private val pageProcessor: SaraminPageProcessor,
    private val browserFactory: BrowserDriverFactory
) {
    private val log = LoggerFactory.getLogger(SaraminCrawlerService::class.java)
    private val baseUrl = "https://www.saramin.co.kr/zf_user/jobs/list/job-category?cat_kewd=84%2C86%2C87&sort=RD&page="

    @Transactional
    suspend fun harvestPage(pageNum: Int): List<JobPosting> = withContext(Dispatchers.IO) {
        val driver = browserFactory.createDriver()
        try {
            log.info("Harvesting Saramin page {}", pageNum)
            // 단일 페이지만 fetch

            val entries = pageProcessor.fetchEntries(pageNum,baseUrl,driver)
            log.info("Fetched {} entries from page {}", entries.size, pageNum)
            return@withContext entries
        } catch (e: Exception) {
            log.error("Error harvesting page {}: {}", pageNum, e.message, e)  // 예외 발생 시 로그 추가
            return@withContext emptyList<JobPosting>()
        } finally {
            driver.quit()
        }
    }
}
