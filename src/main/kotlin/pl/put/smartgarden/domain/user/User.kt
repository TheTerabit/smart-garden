package pl.put.smartgarden.domain.user

import pl.put.smartgarden.domain.device.Device
import javax.persistence.*

@Entity
@Table(name = "users")
open class User {
    @Id
    @Column(name = "id")
    @GeneratedValue
    open var id: String? = null
    open var username: String? = null
    open var email: String? = null
    open var password: String? = null
    open var enabled: Boolean = false
    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    open var device: Device? = null
}
