package pinggu.portforu_crawler.common.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "job_postings")
data class JobPosting(

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val company: String,

    @Column(nullable = false)
    val location: String,

    @Column(nullable = false, unique = true)
    val link: String,

    @Column(nullable = false)
    val salary: String = "-1",

    @Column(nullable = false)
    val duty: String = "-1",

    @Column(nullable = false)
    val employmentType: String = "-1",

    @Column(nullable = false)
    val educationLevel: String = "-1",

    @Column(nullable = false)
    val experienceYears: String = "-1",

    @Column(nullable = false)
    val keyAbilities: String = "-1",

    @Column
    val minExperienceYears: Int? = null,

    @Column
    val maxExperienceYears: Int? = null,

    @Column
    val hiringStartAt: ZonedDateTime? = null,

    @Column
    val hiringEndAt: ZonedDateTime? = null,

    @Column
    val skills: String? = null
) : BaseEntity()
