package pinggu.portforu_crawler.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pinggu.portforu_crawler.jobkorea.domain.JkJobEntry
import pinggu.portforu_crawler.jobkorea.service.JkCrawlerService
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntry
import pinggu.portforu_crawler.saramin.service.SaraminCrawlerService


@RestController
@RequestMapping("/api/crawl")
class CrawlerController(
    private val jkCrawlerService: JkCrawlerService,
    private val saraminCrawlerService: SaraminCrawlerService
) {

    @GetMapping("/jk")
    suspend fun crawlJc(@RequestParam(defaultValue = "1") page: Int): ResponseEntity<List<JkJobEntry>> {
        val entries = jkCrawlerService.crawlPage(page)
        return ResponseEntity.ok(entries)
    }

    @GetMapping("/saramin")
    suspend fun crawlerSaramin(@RequestParam page: Int): List<SaraminJobEntry> {
        return saraminCrawlerService.harvestPage(page)
    }
}