package pinggu.portforu_crawler.messaging.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange

@Configuration
class RabbitMQConfig {

    companion object {
        const val JOB_CLOSING_QUEUE = "job.closing-soon.queue"
        const val CRAWL_COMPLETE_EXCHANGE = "crawl.complete.exchange"
        const val CRAWL_COMPLETE_QUEUE = "crawl.complete.queue"
        const val CRAWL_COMPLETE_ROUTING_KEY = "crawl.complete"
    }

    @Bean
    fun jobClosingQueue(): Queue {
        return Queue(JOB_CLOSING_QUEUE, true) // durable = true
    }

    // 추가: 크롤링 완료 큐/익스체인지/바인딩
    @Bean
    fun crawlCompleteExchange(): TopicExchange {
        return TopicExchange(CRAWL_COMPLETE_EXCHANGE)
    }

    @Bean
    fun crawlCompleteQueue(): Queue {
        return Queue(CRAWL_COMPLETE_QUEUE, true)
    }

    @Bean
    fun crawlCompleteBinding(): Binding {
        return BindingBuilder
            .bind(crawlCompleteQueue())
            .to(crawlCompleteExchange())
            .with(CRAWL_COMPLETE_ROUTING_KEY)
    }
}