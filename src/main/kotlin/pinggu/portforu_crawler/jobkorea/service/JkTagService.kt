package pinggu.portforu_crawler.jobkorea.service


import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pinggu.portforu_crawler.common.SkillTag
import pinggu.portforu_crawler.common.SkillTagRepository
import pinggu.portforu_crawler.common.domain.JobEntry
import java.util.*

@Service
class JkTagService(
    private val skillTagRepository: SkillTagRepository
) {
    companion object {
        private val log = LoggerFactory.getLogger(JkTagService::class.java)
    }

    @Transactional
    fun saveTags(tags: List<String>, jobEntry: JobEntry) {
        tags.asSequence()
            .map { it.trim().lowercase(Locale.getDefault()) }
            .filter { it.isNotBlank() && it != "0" && !it.contains("error") }
            .forEach { tagName ->
                val tag: SkillTag = skillTagRepository.findByName(tagName)
                    ?: skillTagRepository.save(SkillTag(name = tagName))
                log.info("Saved tag: $tag for job entry id: ${jobEntry.id}")
            }
    }
}