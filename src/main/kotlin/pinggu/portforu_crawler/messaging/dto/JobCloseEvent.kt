package pinggu.portforu_crawler.messaging.dto

data class JobCloseEvent(
    val jobId: Long,
    val title: String
)