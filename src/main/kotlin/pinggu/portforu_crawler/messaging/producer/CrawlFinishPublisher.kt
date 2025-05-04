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

        rabbitTemplate.convertAndSend("crawl.complete.queue", message)
        println("[RabbitMQ] 크롤링 완료 메시지 (Direct 방식) 발행됨: $message")
        println("[RabbitMQ] 크롤링 완료 메시지 발행됨: $message")
    }
}