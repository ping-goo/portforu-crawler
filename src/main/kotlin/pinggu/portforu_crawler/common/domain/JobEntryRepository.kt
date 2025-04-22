package pinggu.portforu_crawler.common.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobEntryRepository : JpaRepository<JobEntry, Long> {
    fun findByLink(link: String): JobEntry?
}