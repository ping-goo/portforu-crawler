package pinggu.portforu_crawler.messaging.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.core.Queue

@Configuration
class RabbitMQConfig {

    companion object {
        const val JOB_CLOSING_QUEUE = "job.closing-soon.queue"
    }

    @Bean
    fun jobClosingQueue(): Queue {
        return Queue(JOB_CLOSING_QUEUE, true) // durable = true
    }
}