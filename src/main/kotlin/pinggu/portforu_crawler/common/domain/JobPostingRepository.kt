package pinggu.portforu_crawler.common.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobPostingRepository : JpaRepository<JobPosting, Long> {
    fun findByLink(link: String): JobPosting?
}