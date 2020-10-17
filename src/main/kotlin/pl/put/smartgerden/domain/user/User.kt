package pl.put.smartgerden.domain.user

import pl.put.smartgerden.domain.device.Device
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id")
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    val device: Device?
)
