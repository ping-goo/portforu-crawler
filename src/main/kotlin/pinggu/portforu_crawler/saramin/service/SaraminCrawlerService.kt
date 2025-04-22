package pinggu.portforu_crawler.saramin.service

import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pinggu.portforu_crawler.common.domain.JobEntry
import pinggu.portforu_crawler.config.BrowserDriverFactory

@Service
class SaraminCrawlerService(
    private val pageProcessor: SaraminPageProcessor,
    private val browserFactory: BrowserDriverFactory
) {
    private val log = LoggerFactory.getLogger(SaraminCrawlerService::class.java)
    private val baseUrl = "https://www.saramin.co.kr/zf_user/jobs/list/job-category?cat_kewd=84%2C86%2C87&sort=RD&page="

    @Transactional
    suspend fun harvestPage(pageNum: Int): List<JobEntry> = withContext(Dispatchers.IO) {
        val driver = browserFactory.createDriver()
        try {
            log.info("Harvesting Saramin page {}", pageNum)
            // 단일 페이지만 fetch
            pageProcessor.fetchEntries(pageNum, baseUrl, driver)
        } finally {
            driver.quit()
        }
//        val entries = pageProcessor.fetchEntries(pageNum, baseUrl, driver)
//            entries.forEach { entryRepo.save(it) }
//            entries
//        } finally {
//            driver.quit()
//        }
    }
}
