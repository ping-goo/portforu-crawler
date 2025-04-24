package pinggu.portforu_crawler.stats

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class CrawlerStats {
    val successCount = AtomicInteger(0)
    val parseFailCount = AtomicInteger(0)
    val saveFailCount = AtomicInteger(0)

    fun reset() {
        successCount.set(0)
        parseFailCount.set(0)
        saveFailCount.set(0)
    }
}