package pinggu.portforu_crawler.saramin.domain

import org.springframework.data.jpa.repository.JpaRepository


interface SaraminJobEntryRepository : JpaRepository<SaraminJobEntry, Long> {
    fun existsByLink(link: String): Boolean
}