package pinggu.portforu_crawler.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.util.SlackNotifier
import pinggu.portforu_crawler.stats.CrawlerStats
import java.time.LocalDate

@Component
class SlackDailyReportScheduler(
    private val slackNotifier: SlackNotifier,
    private val crawlerStats: CrawlerStats
) {

    @Scheduled(cron = "0 30 9 * * *") // 매일 오전 9시 30분
    fun sendDailySummary() {
        val date = LocalDate.now()
        val message = """
            📊 JobKorea 크롤링 요약 (${date})
            ✅ 수집 성공: ${crawlerStats.successCount.get()} 건
            ❌ 파싱 실패: ${crawlerStats.parseFailCount.get()} 건
            💾 저장 실패: ${crawlerStats.saveFailCount.get()} 건
        """.trimIndent()

        slackNotifier.send(message)
        crawlerStats.reset()
    }
}