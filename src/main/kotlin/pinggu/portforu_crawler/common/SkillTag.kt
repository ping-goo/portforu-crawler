package pinggu.portforu_crawler.common

import jakarta.persistence.*

// 채용 공고와 관련된 기술 키워드 저장
@Entity
@Table(name = "skill_tag")
data class SkillTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 50, nullable = false)
    val name: String
)