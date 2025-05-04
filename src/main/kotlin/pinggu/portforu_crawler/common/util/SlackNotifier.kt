package pinggu.portforu_crawler.common.util

import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.springframework.beans.factory.annotation.Value

@Component
class SlackNotifier(
    @Value("\${slack.webhook.url}")
    private val webhookUrl: String
) {

    private val client = HttpClient.newHttpClient()

    fun send(message: String) {
        if (webhookUrl.isBlank()) {
            println("Slack Webhook URL이 설정되어 있지 않습니다.")
            return
        }

        val payload = """{"text": "$message"}"""
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .header("Content-Type", "application/json")
            .build()

        client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
    }
}