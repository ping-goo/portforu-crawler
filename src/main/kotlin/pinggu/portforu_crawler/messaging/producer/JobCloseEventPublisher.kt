package pinggu.portforu_crawler.messaging.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.messaging.dto.JobCloseEvent
import org.springframework.amqp.core.AmqpTemplate
import pinggu.portforu_crawler.messaging.config.RabbitMQConfig

@Component
class JobCloseEventPublisher(
    private val amqpTemplate: AmqpTemplate,
    private val objectMapper: ObjectMapper
) {
    fun publish(event: JobCloseEvent) {
        val message = objectMapper.writeValueAsString(event)
        amqpTemplate.convertAndSend(RabbitMQConfig.JOB_CLOSING_QUEUE, message)
        println("RabbitMQ [job.closing-soon.queue] 발행: $message")
    }
}