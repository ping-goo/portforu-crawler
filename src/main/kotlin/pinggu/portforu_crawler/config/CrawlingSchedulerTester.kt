package pinggu.portforu_crawler.config

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class CrawlingSchedulerTester(
    private val scheduler: CrawlingScheduler
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        scheduler.scheduleDailyCrawling()
    }
}