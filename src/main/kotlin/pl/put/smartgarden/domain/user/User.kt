package pl.put.smartgarden.domain.user

import org.hibernate.annotations.GenericGenerator
import pl.put.smartgarden.domain.device.Device
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
        var username: String,
        var email: String,
        var password: String,
        var enabled: Boolean = false,
        @OneToOne
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        var device: Device? = null,
        @Id
        @Column(name = "id")
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: String? = null)

@Entity
@Table(name = "VerificationTokens")
class VerificationToken(
        var token: String,
        @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        open var user: User,
        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        var id: String? = null)
