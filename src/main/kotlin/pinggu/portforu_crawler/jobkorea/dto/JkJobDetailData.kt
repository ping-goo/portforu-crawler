package pinggu.portforu_crawler.jobkorea.dto

data class JkJobDetailData(
    val company: String,
    val experience: String,
    val education: String,
    val keyAbilities: String,
    val preference: String,
    val employmentType: String,
    val salary: String,
    val location: String,
    val startDate: String?,
    val endDate: String?,
    val skills: String
)