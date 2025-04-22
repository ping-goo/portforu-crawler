package pinggu.portforu_crawler.jobkorea.service

import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pinggu.portforu_crawler.common.domain.JobEntry
import pinggu.portforu_crawler.config.BrowserDriverFactory
import java.time.Duration

@Service
class JkCrawlerService(
    private val jobEntryProcessor: JkJobEntryProcessor,
    private val browserDriverFactory: BrowserDriverFactory,
    private val jkFilterManager: JkFilterManager
) {
    private val logger = LoggerFactory.getLogger(JkCrawlerService::class.java)

    /**
     * 필터는 첫 페이지(1)에서만 걸고,
     * 그 뒤엔 요청한 page 파라미터에 해당하는 페이지만 크롤링.
     */
    @Transactional
    suspend fun crawlPage(page: Int = 1): List<JobEntry> = withContext(Dispatchers.IO) {
        val driver = browserDriverFactory.createDriver()
        val wait = WebDriverWait(driver, Duration.ofSeconds(30))
        val processedJobs = mutableListOf<JobEntry>()
        try {
            // 1) 필터 적용용 첫 페이지
            val filterUrl = "https://www.jobkorea.co.kr/recruit/joblist?menucode=search#anchorGICnt_1"
            driver.get(filterUrl)
            jkFilterManager.applyFilters(wait, driver)

            // 2) 요청한 단일 페이지로 이동
            val targetUrl = "https://www.jobkorea.co.kr/recruit/joblist?menucode=search#anchorGICnt_$page"
            logger.info("Crawling only page $page → $targetUrl")
            driver.get(targetUrl)

            // 3) 해당 페이지만 목록 요소 처리
            val jobElements = driver.findElements(By.cssSelector("strong a.link.normalLog"))
            for (el in jobElements) {
                val entry = jobEntryProcessor.processJobEntry(el)
                if (entry != null) {
                    processedJobs += entry
                }
            }
        } finally {
            driver.quit()
        }
        processedJobs
    }
}