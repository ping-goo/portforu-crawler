package pinggu.portforu_crawler.messaging.dto

data class JobCloseEvent(
    val jobPostingId: Long,
    val jobTitle: String
)