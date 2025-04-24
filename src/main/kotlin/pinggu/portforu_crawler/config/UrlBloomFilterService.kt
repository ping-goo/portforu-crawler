package pinggu.portforu_crawler.config

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class UrlBloomFilterService(
    private val redisTemplate: StringRedisTemplate
) {
    private val redisKey = "bloom:processedUrls"
    private val hashCount = 5
    private val bitArraySize = 10_000_000  // 비트 배열 크기

    // 해시 함수
    private fun hash(value: String, seed: Int): Int {
        val digest = MessageDigest.getInstance("MD5")
        digest.update((value + seed).toByteArray())
        val hashBytes = digest.digest()

        var hash = 0L
        for (b in hashBytes) {
            hash = (hash * 31 + (b.toInt() and 0xff)) and 0xffffffffL
        }

        return (hash % bitArraySize).toInt()
    }

    fun isNewUrl(url: String): Boolean {
        return try {
            val indexes = (0 until hashCount).map { hash(url, it) }

            var existsBits: List<Boolean> = emptyList()
            redisTemplate.executePipelined({ connection ->
                indexes.forEach { i ->
                    connection.getBit(redisKey.toByteArray(), i.toLong())
                }
                null
            })?.let {
                existsBits = it.map { b -> b == true }
            }

            val alreadyExists = existsBits.all { it }

            if (!alreadyExists) {
                redisTemplate.executePipelined({ connection ->
                    indexes.forEach { i ->
                        connection.setBit(redisKey.toByteArray(), i.toLong(), true)
                    }
                    null
                })
            }

            if (alreadyExists) {
                println("[Bloom] 기존 URL 스킵: $url")
            } else {
                println("[Bloom] 신규 URL 등록: $url")
            }

            !alreadyExists
        } catch (e: Exception) {
            println(" Bloom 필터 내부 오류 → URL 우선 처리 허용: $url / ${e.message}")
            true
        }
    }
}