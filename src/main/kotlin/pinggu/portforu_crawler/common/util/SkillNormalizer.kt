package pinggu.portforu_crawler.common.util

object SkillNormalizer {

    fun normalize(raw: String): List<String> {
        return raw.lowercase()
            .split(",", " ", "/", "|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}