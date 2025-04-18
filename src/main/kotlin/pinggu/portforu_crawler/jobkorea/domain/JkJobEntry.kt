package pinggu.portforu_crawler.jobkorea.domain

import jakarta.persistence.*

@Entity
@Table(name = "jk_job_entry")
data class JkJobEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 255, nullable = false)
    val title: String,

    @Column(length = 255, nullable = false)
    val company: String,

    // 상세 페이지 URL – 중복 방지를 위해 unique 처리
    @Column(length = 255, nullable = false)
    val link: String,

    // 목록 페이지 상의 직무 정보
    val duty: String,

    // 지원자격 관련 정보 (경력, 학력, 핵심역량, 우대)
    val experience: String,

    val education: String,

    val keyAbilities: String,

    val preference: String,

    // 근무조건 관련 정보 (고용형태, 급여, 지역)
    val employmentType: String,

    val salary: String,

    val location: String,

    val skills: String? = null,

    // 공고 기간
    val startDate: String?,
    val endDate: String?
) {
    companion object {
        fun create(
            title: String,
            company: String,
            link: String,
            duty: String,
            experience: String,
            education: String,
            keyAbilities: String,
            preference: String,
            employmentType: String,
            salary: String,
            location: String,
            skills: String?,
            startDate: String?,
            endDate: String?
        ): JkJobEntry {
            return JkJobEntry(
                title = title,
                company = company,
                link = link,
                duty = duty,
                experience = experience,
                education = education,
                keyAbilities = keyAbilities,
                preference = preference,
                employmentType = employmentType,
                salary = salary,
                location = location,
                skills = skills,
                startDate = startDate,
                endDate = endDate
            )
        }
    }
}