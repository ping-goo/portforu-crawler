package pinggu.portforu_crawler.saramin.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "saramin_job_entry")
data class SaraminJobEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 255, nullable = false)
    val title: String,

    @Column(length = 255, nullable = false, unique = true)
    val link: String,

    val company: String,
    val location: String,
    val salary: Int,
    val employmentType: String,
    val educationLevel: String,
    val minExperienceYears: Int?,
    val maxExperienceYears: Int?,
    val duty: String,
    val hiringStartAt: ZonedDateTime?,
    val hiringEndAt: ZonedDateTime?
) {
    companion object {
        fun toEntity(
            title: String,
            company: String,
            location: String,
            salary: Int,
            employmentType: String,
            educationLevel: String,
            link: String,
            minExperienceYears: Int?,
            maxExperienceYears: Int?,
            duty: String,
            hiringStartAt: ZonedDateTime?,
            hiringEndAt: ZonedDateTime?
        ): SaraminJobEntry {
            return SaraminJobEntry(
                title = title,
                company = company,
                location = location,
                salary = salary,
                employmentType = employmentType,
                educationLevel = educationLevel,
                link = link,
                minExperienceYears = minExperienceYears,
                maxExperienceYears = maxExperienceYears,
                duty = duty,
                hiringStartAt = hiringStartAt,
                hiringEndAt = hiringEndAt
            )
        }
    }
}
