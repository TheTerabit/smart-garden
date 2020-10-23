package pl.put.smartgarden.domain.user

import org.hibernate.annotations.GenericGenerator
import pl.put.smartgarden.domain.device.Device
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(
    var username: String,
    var email: String,
    var password: String,
    var enabled: Boolean,
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var device: Device? = null,
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    val id: String? = null
)

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
    var id: String? = null
)
