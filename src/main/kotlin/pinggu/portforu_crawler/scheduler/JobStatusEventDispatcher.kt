package pinggu.portforu_crawler.scheduler

import org.springframework.stereotype.Component
import pinggu.portforu_crawler.messaging.dto.JobCloseEvent
import pinggu.portforu_crawler.messaging.producer.JobCloseEventPublisher

@Component
class JobStatusEventDispatcher(
    private val jobCloseEventPublisher: JobCloseEventPublisher
) {
    fun dispatchClosingSoonEvent(jobId: Long, title: String) {
        val event = JobCloseEvent(jobId, title)
        jobCloseEventPublisher.publish(event)
    }
}