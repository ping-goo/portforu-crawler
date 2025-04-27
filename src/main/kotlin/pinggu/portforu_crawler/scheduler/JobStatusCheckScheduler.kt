package pinggu.portforu_crawler.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pinggu.portforu_crawler.common.domain.JobPostingRepository
import java.time.LocalDate
import java.time.ZoneId

@Component
class JobStatusCheckScheduler(
    private val jobPostingRepository: JobPostingRepository,
    private val jobStatusEventDispatcher: JobStatusEventDispatcher
) {

    @Scheduled(cron = "0 44 7 * * *") // 매일 10시
    fun checkClosingSoonJobs() {
        println("✅ [스케줄러] 오늘 마감하는 공고 체크 시작")

        val zoneId = ZoneId.systemDefault()
        val tomorrow = LocalDate.now(zoneId).plusDays(1)

        val startOfTomorrow = tomorrow.atStartOfDay(zoneId)
        val endOfTomorrow = tomorrow.atTime(23, 59, 59).atZone(zoneId)

        val closingSoonJobs = jobPostingRepository.findByHiringEndAtBetween(startOfTomorrow, endOfTomorrow)

        println("✅ [스케줄러] 오늘 마감 공고 수: ${closingSoonJobs.size}")

        closingSoonJobs.forEach { job ->
            println("✅ [스케줄러] 오늘 마감 예정 공고: ${job.title}")
            jobStatusEventDispatcher.dispatchClosingSoonEvent(
                job.id!!, job.title
            )
        }
    }
}