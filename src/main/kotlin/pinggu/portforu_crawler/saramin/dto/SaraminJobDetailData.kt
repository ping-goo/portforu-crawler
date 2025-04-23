package pinggu.portforu_crawler.saramin.dto

import java.time.ZonedDateTime

data class SaraminJobDetailData(
    val company: String,
    val location: String,
    val employmentType: String,
    val educationLevel: String,
    val salary: String,
    val minExperienceYears: Int?,
    val maxExperienceYears: Int?,
    val hiringStartAt: ZonedDateTime?,
    val hiringEndAt: ZonedDateTime?
)