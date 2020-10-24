package pl.put.smartgarden.domain.device

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "devices")
class Device(
    var guid: String,
    @Column(name = "user_id")
    var userId: String?,
    var latitude: Double?,
    var longitude: Double?,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor>,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var groups: MutableList<Group>
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    lateinit var id: String
}

@Entity
@Table(name = "sensors")
class Sensor(
    var type: String,
    var number: Int,
    @Column(name = "device_id")
    var deviceId: String,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    var measures: MutableList<Measure>,
    @Column(name = "group_id")
    var groupId: String?
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    lateinit var id: String
}

@Entity
@Table(name = "measures")
class Measure(
    var timestamp: Instant,
    var type: String,
    var unit: String,
    var value: Double,
    @Column(name = "sensor_id")
    var sensorId: String
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    lateinit var id: String
}

@Entity
@Table(name = "groups")
class Group(
    var settings: String,//TODO("Not yet implemented")
    @Column(name = "device_id")
    var deviceId: String,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor>,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var waterings: MutableList<Watering>
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    lateinit var id: String
}

@Entity
@Table(name = "waterings")
class Watering(
    var timestamp: Instant,
    @Column(name = "group_id")
    var groupId: String,
    var planned: Boolean
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    lateinit var id: String
}