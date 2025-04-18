package pinggu.portforu_crawler.jobkorea.domain

import org.springframework.data.jpa.repository.JpaRepository

interface JkJobEntryRepository : JpaRepository<JkJobEntry, Long> {
    fun findByLink(link: String): JkJobEntry?
}