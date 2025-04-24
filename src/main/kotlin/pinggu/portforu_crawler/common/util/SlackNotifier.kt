package pinggu.portforu_crawler.common.util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class SlackNotifier(private val webhookUrl: String) {

    private val client = HttpClient.newHttpClient()

    fun send(message: String) {
        val payload = """{"text": "$message"}"""
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .header("Content-Type", "application/json")
            .build()

        client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
    }
}