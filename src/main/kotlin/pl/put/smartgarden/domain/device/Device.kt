package pl.put.smartgarden.domain.device

import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "devices")
data class Device(
    @Id
    @Column(name = "id")
    val id: String,
    @Column(name = "user_id")
    val userId: String?,
    val latitude: Double?,
    val longitude: Double?,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name="device_id", referencedColumnName = "id")
    val sensors: List<Sensor>,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name="device_id", referencedColumnName = "id")
    val groups: List<Group>
)

@Entity
@Table(name = "sensors")
data class Sensor(
    @Id
    @Column(name = "id")
    val id: String,
    val type: String,
    val number: Int,
    @Column(name = "device_id")
    val deviceId: String,
    @Column(name = "group_id")
    val groupId: String?,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name="sensor_id", referencedColumnName = "id")
    val measures: List<Measure>
)

@Entity
@Table(name = "measures")
data class Measure(
    @Id
    @Column(name = "id")
    val id: String,
    val timestamp: Instant,
    val type: String,
    val unit: String,
    val value: Double,
    @Column(name = "sensor_id")
    val sensorId: String
)

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @Column(name = "id")
    val id: String,
    val settings: String,//TODO("Not yet implemented")
    @Column(name = "device_id")
    val deviceId: String,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name="group_id", referencedColumnName = "id")
    val sensors: List<Sensor>,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name="group_id", referencedColumnName = "id")
    val waterings: List<Watering>
)

@Entity
@Table(name = "waterings")
data class Watering(
    @Id
    @Column(name = "id")
    val id: String,
    val timestamp: Instant,
    @Column(name = "group_id")
    val groupId: String,
    val planned: Boolean
)