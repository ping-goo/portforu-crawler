package pinggu.portforu_crawler.saramin

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

object SaraminScroller {

    fun scrollToBottom(driver: WebDriver) {
        val jsExecutor = driver as JavascriptExecutor
        var prevHeight = jsExecutor.executeScript("return document.body.scrollHeight") as Long

        while (true) {
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);")
            Thread.sleep(2000)  // 동적 콘텐츠 로딩 대기
            val newHeight = jsExecutor.executeScript("return document.body.scrollHeight") as Long
            if (newHeight == prevHeight) break
            prevHeight = newHeight
        }
    }
}