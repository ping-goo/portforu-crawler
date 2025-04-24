package pinggu.portforu_crawler.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pinggu.portforu_crawler.common.util.SlackNotifier

@RestController
@RequestMapping("/api/test")
class SlackTestController(private val slackNotifier: SlackNotifier) {

    @GetMapping("/slack")
    fun testSlack(): ResponseEntity<String> {
        slackNotifier.send("크롤링 실패")
        return ResponseEntity.ok("Slack 알림 전송됨")
    }
}