package pinggu.portforu_crawler.messaging.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter

@Configuration
class RabbitMQConfig {

    companion object {
        const val JOB_CLOSING_DELAY_QUEUE = "job.closing.delay.queue"
        const val JOB_CLOSING_FINAL_QUEUE = "job.closing-soon.queue"
        const val CRAWL_COMPLETE_EXCHANGE = "crawl.complete.exchange"
        const val CRAWL_COMPLETE_QUEUE = "crawl.complete.queue"
        const val CRAWL_COMPLETE_ROUTING_KEY = "crawl.complete"
    }

    // Delay Queue (24시간 후 메시지를 소비 큐로 전달)
    @Bean
    fun jobClosingDelayQueue(): Queue {
        val args = mapOf(
            "x-dead-letter-exchange" to "",  // 기본 exchange 사용
            "x-dead-letter-routing-key" to JOB_CLOSING_FINAL_QUEUE // 최종 큐로 라우팅
        )
        return Queue(JOB_CLOSING_DELAY_QUEUE, true, false, false, args)
    }

    @Bean
    fun jobClosingQueue(): Queue {
        return Queue(JOB_CLOSING_FINAL_QUEUE, true) // durable = true
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

    // 메시지 컨버터 추가 (JSON 직렬화 사용)
    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    // RabbitTemplate에 컨버터 주입
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter
        return template
    }
}