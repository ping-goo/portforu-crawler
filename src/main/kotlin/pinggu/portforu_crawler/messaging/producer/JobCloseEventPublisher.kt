package pinggu.portforu_crawler.messaging.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.messaging.dto.JobCloseEvent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import pinggu.portforu_crawler.messaging.config.RabbitMQConfig

@Component
class JobCloseEventPublisher(
    private val amqpTemplate: AmqpTemplate,
    private val objectMapper: ObjectMapper
) {
    fun publishWithDelay(event: JobCloseEvent, delayMillis: Long) {
        val body = objectMapper.writeValueAsBytes(event)
        val message = MessageBuilder.withBody(body)
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setExpiration(delayMillis.toString()) // TTL(ms)
            .build()

        amqpTemplate.send(RabbitMQConfig.JOB_CLOSING_DELAY_QUEUE, message)
        println("Delay 발송 (delay=${delayMillis}ms) → ${RabbitMQConfig.JOB_CLOSING_DELAY_QUEUE}: $event")
    }

    fun publishImmediately(event: JobCloseEvent) {
        val body = objectMapper.writeValueAsBytes(event)
        val message = MessageBuilder.withBody(body)
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .build()

        amqpTemplate.send(RabbitMQConfig.JOB_CLOSING_FINAL_QUEUE, message)
        println(" 즉시 발송 → ${RabbitMQConfig.JOB_CLOSING_FINAL_QUEUE}: $event")
    }
}