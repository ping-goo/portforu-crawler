package pinggu.portforu_crawler.common.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(updatable = false)
    var createdAt: Instant? = null

    @LastModifiedDate
    @Column
    var updatedAt: Instant? = null

    @Column
    var deletedAt: Instant? = null

    fun isDeleted(): Boolean = deletedAt != null

    fun delete(): Long? {
        this.deletedAt = Instant.now()
        return this.id
    }

    fun restore() {
        this.deletedAt = null
    }
}