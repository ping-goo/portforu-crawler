package pinggu.portforu_crawler.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pinggu.portforu_crawler.common.util.SlackNotifier

@Configuration
class SlackConfig {

    @Bean
    fun slackNotifier(): SlackNotifier {
        return SlackNotifier("https://hooks.slack.com/services/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")
    }
}