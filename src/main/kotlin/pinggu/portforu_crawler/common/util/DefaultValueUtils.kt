package pinggu.portforu_crawler.common.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * null 이거나 빈 문자열이면 기본값을 반환하는 확장 함수
 */
fun String?.orDefault(default: String): String =
    this?.takeIf { it.isNotBlank() } ?: default

/**
 * null 이면 기본값을 반환하는 Int 확장 함수
 */
fun Int?.orDefault(default: Int): Int =
    this ?: default

/**
 * null 이면 기본 ZonedDateTime 값(1970-01-01T00:00+09:00)을 반환하는 확장 함수
 */
fun ZonedDateTime?.orDefault(default: ZonedDateTime = defaultZonedDateTime()): ZonedDateTime =
    this ?: default

/**
 * 기본 ZonedDateTime 값을 제공 (1970-01-01 기준)
 */
fun defaultZonedDateTime(): ZonedDateTime =
    ZonedDateTime.of(LocalDate.of(1970, 1, 1).atStartOfDay(), ZoneId.of("Asia/Seoul"))