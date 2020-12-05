package pl.put.smartgarden.domain.device

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.Collections
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
    var userId: Int,
    var latitude: Double,
    var longitude: Double
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor> = Collections.emptyList()
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var areas: MutableList<Area> = Collections.emptyList()
}

@Entity
@Table(name = "sensors")
class Sensor(
    var type: SensorType,
    var guid: String,
    @Column(name = "device_id")
    var deviceId: Int
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
    var isActive: Boolean = true
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    var measures: MutableList<Measure> = Collections.emptyList()
    @Column(name = "area_id")
    var areaId: Int? = null
}

@Entity
@Table(name = "measures")
class Measure(
    var timestamp: Instant,
    var value: Int,
    @Column(name = "sensor_id")
    var sensorId: Int
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
}

@Entity
@Table(name = "areas")
class Area(
    var settings: String,//TODO("Not yet implemented")
    @Column(name = "device_id")
    var deviceId: Int,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor>,
    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    var irrigations: MutableList<Irrigation>
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
}

@Entity
@Table(name = "irrigations")
class Irrigation(
    var timestamp: Instant,
    @Column(name = "area_id")
    var areaId: Int,
    var planned: Boolean
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
}