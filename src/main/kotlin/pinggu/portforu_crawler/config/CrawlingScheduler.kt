package pinggu.portforu_crawler.config

import org.springframework.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.ParallelCrawlerService
import kotlinx.coroutines.runBlocking

@Component
class CrawlingScheduler(
    private val parallelCrawlerService: ParallelCrawlerService
) {
    private val log = LoggerFactory.getLogger(CrawlingScheduler::class.java)

    /**
     * 매일 오전 8시에 JobKorea + Saramin 크롤링 동시 실행
     */
    @Scheduled(cron = "0 0 8 * * *")
    fun scheduleDailyCrawling() {
        runBlocking {
            log.info("[스케줄링] 오전 8시 크롤링 시작")
            try {
                val jkPage = 1
                val srPage = 1
                parallelCrawlerService.crawlBoth(jkPage, srPage)
                log.info("[스케줄링] 크롤링 완료")
            } catch (e: Exception) {
                log.error("[스케줄링] 크롤링 실패: ${e.message}", e)
            }
        }
    }
}