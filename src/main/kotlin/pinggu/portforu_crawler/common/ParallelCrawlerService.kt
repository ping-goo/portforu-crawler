package pinggu.portforu_crawler.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import pinggu.portforu_crawler.jobkorea.domain.JkJobEntry
import pinggu.portforu_crawler.jobkorea.service.JkCrawlerService
import pinggu.portforu_crawler.saramin.domain.SaraminJobEntry
import pinggu.portforu_crawler.saramin.service.SaraminCrawlerService
import kotlinx.coroutines.async

@Service
class ParallelCrawlerService (
    private val jkCrawlerService : JkCrawlerService,
    private val saraminCrawlerService : SaraminCrawlerService
){
    suspend fun crawlBoth(
        jkPage : Int = 1,
        saraminPage: Int = 1
    ): Pair<List<JkJobEntry>,List<SaraminJobEntry>> = coroutineScope {
        // IO 바운드 작업
        val jkDeferred = async(Dispatchers.IO){
            jkCrawlerService.crawlPage(jkPage)
        }
        val srDeferred = async(Dispatchers.IO) {
            saraminCrawlerService.harvestPage(saraminPage)
        }

        // 두 크롤러가 끝날 때까지 병렬 대기
        val jkResult = jkDeferred.await()
        val srResult = srDeferred.await()
        jkResult to srResult
    }

}