package pinggu.portforu_crawler.messaging.producer

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CrawlFinishPublisher(
    private val rabbitTemplate: RabbitTemplate
) {
    fun publishCrawlFinished(site: String, crawledCount: Int) {
        val message = mapOf(
            "site" to site,
            "crawledCount" to crawledCount,
            "finishedAt" to LocalDateTime.now().toString()
        )
        rabbitTemplate.convertAndSend(
            pinggu.portforu_crawler.messaging.config.RabbitMQConfig.CRAWL_COMPLETE_EXCHANGE,
            pinggu.portforu_crawler.messaging.config.RabbitMQConfig.CRAWL_COMPLETE_ROUTING_KEY,
            message
        )
    }
}