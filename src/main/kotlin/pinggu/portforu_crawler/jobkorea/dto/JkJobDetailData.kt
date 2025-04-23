package pinggu.portforu_crawler.jobkorea.dto

import java.time.ZonedDateTime

data class JkJobDetailData(
    val company: String,
    val experience: String,
    val educationLevel: String,
    val keyAbilities: String,
    val preference: String,
    val employmentType: String,
    val salary: String,
    val location: String,
    val hiringStartAt: ZonedDateTime?,
    val hiringEndAt: ZonedDateTime?,
    val skills: String
)