package pinggu.portforu_crawler.jobkorea.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.jobkorea.dto.JkJobDetailData
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class JkDetailParser {

    fun parseDetail(html: String): JkJobDetailData? {
        return try {
            val doc: Document = Jsoup.parse(html)

            // 회사명 추출
            val company = doc.selectFirst("span.coName")?.text()?.trim() ?: ""

            // 메인 dl.tbList 내에서 경력, 학력, 스킬, 핵심역량, 우대 등을 추출
            val dl = doc.selectFirst("dl.tbList")
            var experience = ""
            var educationLevel = ""
            var skills = ""
            var keyAbilities = ""
            var preference = ""

            dl?.select("dt")?.forEach { dt ->
                val keyText = dt.text().trim()
                val valueText = dt.nextElementSibling()?.text()?.trim() ?: ""
                when (keyText) {
                    "경력" -> experience = valueText
                    "학력" -> educationLevel = valueText
                    "스킬" -> skills = valueText
                    "핵심역량" -> {
                        keyAbilities = valueText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .joinToString(", ")
                    }
                    "우대" -> preference = valueText
                }
            }

            // 근무조건(고용형태, 급여, 지역) 파싱
            val employmentType = doc.select("dt:contains(고용형태) + dd ul.addList li strong")
                .eachText().joinToString(", ")
            val salaryText = doc.select("dt:contains(급여) + dd").text().trim()
            val locationText = doc.select("dt:contains(지역) + dd a").text().trim()


            // 날짜 파싱
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val zoneId = ZoneId.of("Asia/Seoul")

            var hiringStartAt: ZonedDateTime? = null
            var hiringEndAt: ZonedDateTime? = null
            val dateElement = doc.selectFirst("dl.date")
            dateElement?.select("dt")?.forEach { dt ->
                val dtText = dt.text().trim()
                val dateStr = dt.nextElementSibling()?.selectFirst("span.tahoma")?.text()?.trim()
                if (dtText.contains("시작일")) {
                    hiringStartAt = dateStr?.let {
                        ZonedDateTime.of(LocalDate.parse(it, formatter).atStartOfDay(), zoneId)
                    }
                } else if (dtText.contains("마감일")) {
                    hiringEndAt = dateStr?.let {
                        ZonedDateTime.of(LocalDate.parse(it, formatter).atStartOfDay(), zoneId)
                    }
                }
            }

            JkJobDetailData(
                company = company,
                experience = experience,
                educationLevel = educationLevel,
                keyAbilities = keyAbilities,
                preference = preference,
                employmentType = employmentType,
                salary = salaryText,
                location = locationText,
                hiringStartAt = hiringStartAt,
                hiringEndAt = hiringEndAt,
                skills = skills
            )
        } catch (e: Exception) {
            println("상세 페이지 파싱 오류: ${e.message}")
            null
        }
    }
}