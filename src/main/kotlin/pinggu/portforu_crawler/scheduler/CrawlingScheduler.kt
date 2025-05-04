package pinggu.portforu_crawler.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.ParallelCrawlerService
import kotlinx.coroutines.runBlocking
import pinggu.portforu_crawler.common.util.SlackNotifier
import pinggu.portforu_crawler.messaging.producer.CrawlFinishPublisher
import pinggu.portforu_crawler.stats.CrawlerStats

@Component
class CrawlingScheduler(
    private val parallelCrawlerService: ParallelCrawlerService,
    private val crawlFinishPublisher: CrawlFinishPublisher,
    private val slackNotifier: SlackNotifier,
    private val crawlerStats: CrawlerStats
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

                crawlFinishPublisher.publishCrawlFinished("AllSites", 0)
                log.info("[스케줄링] 크롤링 완료 알림 발행")

                // 여기서 Slack 알림 바로 전송
                val message = """
                    JobKorea 크롤링 완료
                    수집 성공: ${crawlerStats.successCount.get()} 건
                    파싱 실패: ${crawlerStats.parseFailCount.get()} 건
                    저장 실패: ${crawlerStats.saveFailCount.get()} 건
                """.trimIndent()
                slackNotifier.send(message)
                crawlerStats.reset()

            } catch (e: Exception) {
                log.error("[스케줄링] 크롤링 실패: ${e.message}", e)
                slackNotifier.send("크롤링 실패: ${e.message}")
            }
        }
    }
}