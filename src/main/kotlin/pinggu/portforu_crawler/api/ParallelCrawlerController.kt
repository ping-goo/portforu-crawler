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
    /**
     * GET /api/crawl/both?jkPage=1&saraminPage=2
     * 두 크롤러를 동시에 실행하고, JSON으로 결과를 묶어 반환합니다.
     */
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