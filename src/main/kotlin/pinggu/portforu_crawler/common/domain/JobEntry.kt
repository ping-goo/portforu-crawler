package pinggu.portforu_crawler.common.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "common_job_entry")
data class JobEntry (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 255, nullable = false)
    val title: String,

    @Column(length = 255, nullable = false)
    val company: String,

    @Column(length = 255, nullable = false)
    val location: String,

    // 상세 페이지 URL – 중복 방지를 위해 unique 처리
    @Column(length = 255, nullable = false, unique = true)
    val link: String,

    // 근무 조건 관련 정보
    @Column(nullable = false)
    val salary: String = "-1",

    @Column(nullable = false)
    val duty: String = "-1",

    @Column(nullable = false)
    val employmentType: String = "-1",

    // 지원자격 정보
    @Column(nullable = false)
    val educationLevel: String = "-1",

    @Column(nullable = false)
    val experienceYears: String = "-1",  // JobKorea 전용

    @Column(nullable = false)
    val keyAbilities: String = "-1",     // JobKorea 전용

    @Column(nullable = false)
    val minExperienceYears: Int? = null,    // Saramin 전용

    @Column(nullable = false)
    val maxExperienceYears: Int? = null,    // Saramin 전용

    @Column(nullable = true)
    val hiringStartAt: ZonedDateTime? = null,

    @Column(nullable = true)
    val hiringEndAt: ZonedDateTime? = null,

    @Column(nullable = true)
    val skills: String? = null           // 기술 태그 (JobKorea 위주)
)