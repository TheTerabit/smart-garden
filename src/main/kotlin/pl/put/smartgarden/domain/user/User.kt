package pl.put.smartgarden.domain.user

import org.hibernate.annotations.GenericGenerator
import pl.put.smartgarden.domain.device.Device
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
open class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    open var id: String? = null
    open var username: String? = null
    open var email: String? = null
    open var password: String? = null
    open var enabled: Boolean = false

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var device: Device? = null
}

@Entity
@Table(name = "VerificationTokens")
open class VerificationToken {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    open var id: String? = null
    open var token: String? = null

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    open var user: User? = null
    open var expiryDate: LocalDateTime? = calculateExpiryDate(60L)

    constructor(token: String?, user: User?) {
        this.token = token
        this.user = user
    }


    private fun calculateExpiryDate(expiryTimeInMinutes: Long): LocalDateTime? {
        val now = LocalDateTime.now()
        return now.plusMinutes(expiryTimeInMinutes)
    }
}