package pinggu.portforu_crawler.common.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @CreatedDate
    @Column(updatable = false, nullable = false)
    open var createdAt: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    open var updatedAt: Instant? = null

    @Column(name = "is_deleted", nullable = false)
    open var isDeleted: Boolean = false

    fun softDelete(): Long? {
        this.isDeleted = true
        return this.id
    }

    fun restore() {
        this.isDeleted = false
    }
}
