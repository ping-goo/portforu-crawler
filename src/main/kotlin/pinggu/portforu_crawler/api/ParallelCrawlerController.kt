package pinggu.portforu_crawler.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pinggu.portforu_crawler.common.ParallelCrawlerService

@RestController
@RequestMapping("/api/crawl")
class ParallelCrawlerController(
    private val parallelCrawlerService: ParallelCrawlerService
) {

    @GetMapping("/both")
    suspend fun crawlBoth(
        @RequestParam(defaultValue = "1") jkPage: Int,
        @RequestParam(defaultValue = "1") saraminPage: Int
    ): ResponseEntity<Map<String, List<Any>>> {
        val (jkList, srList) = parallelCrawlerService.crawlBoth(jkPage, saraminPage)
        return ResponseEntity.ok(
            mapOf(
                "jobKorea" to jkList,
                "saramin"  to srList
            )
        )
    }
}