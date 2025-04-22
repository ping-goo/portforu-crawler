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
    val salary: String,
    val duty: String,
    val employmentType: String,

    // 지원자격 정보
    val educationLevel: String,
    val experienceYears: String? = null, // Jk
    val keyAbilities: String? = null, // Jk

    // 날짜 연산 할 것을 고려해서 ZonedDateTime 으로 함(jk-startDate, endDate)
    val hiringStartAt: ZonedDateTime? = null,
    val hiringEndAt: ZonedDateTime? = null,

    // saramin 필드 - 경력
    val minExperienceYears: Int? = null,
    val maxExperienceYears: Int? = null,

    // Jk 필드 - 스킬 태그
    val skills: String? = null
)