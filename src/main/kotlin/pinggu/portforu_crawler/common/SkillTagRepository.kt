package pinggu.portforu_crawler.common

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// JPA를 이용해 Keyword 엔티티에 대한 CRUD 작업을 수행
// 메서드로 중복 여부 확인
@Repository
interface SkillTagRepository : JpaRepository<SkillTag, Long> {
    fun findByName(name: String): SkillTag?
}