package pl.put.smartgarden.domain.device

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
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
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor> = Collections.emptyList()
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    var areas: MutableList<Area> = Collections.emptyList()

    override fun equals(other: Any?): Boolean =
        (other is Device)
            && id == other.id
            //&& sensors == other.sensors
            //&& areas.equals(other.areas)
            && userId == other.userId
            && latitude == other.latitude
            && guid == other.guid
            && longitude == other.longitude

    override fun hashCode(): Int = Objects.hash(id)
}

@Entity
@Table(name = "weathers")
class Weather(
    @Column(name = "device_id")
    var deviceId: Int,
    var byWhen: Instant,
    var isGoingToRain: Boolean
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
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
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    var measures: MutableList<Measure> = Collections.emptyList()
    @Column(name = "area_id")
    var areaId: Int? = null

    override fun equals(other: Any?): Boolean =
        (other is Sensor)
            && id == other.id
            && isActive == other.isActive
            //&& measures == other.measures
            && areaId == other.areaId
            && type == other.type
            && guid == other.guid
            && deviceId == other.deviceId

    override fun hashCode(): Int = Objects.hash(id)
}

@Entity
@Table(name = "measures")
class Measure(
    var timestamp: Instant,
    var value: Int,
    @Column(name = "sensor_id")
    var sensorId: Int,
    @Column(name = "area_id")
    var areaId: Int? = null)
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id: Int? = null

    override fun equals(other: Any?): Boolean =
        (other is Measure)
            && id == other.id
            && timestamp.epochSecond == other.timestamp.epochSecond
            && value == other.value
            && sensorId == other.sensorId

    override fun hashCode(): Int = Objects.hash(id)
}

@Entity
@Table(name = "areas")
class Area(
    @OneToOne(targetEntity = AreaSettings::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    var settings: AreaSettings,
    @Column(name = "device_id")
    var deviceId: Int,
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    var sensors: MutableList<Sensor>,
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    var measures: MutableList<Measure>,
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
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
    var amount: Int
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
}

@Entity
@Table(name = "area_settings")
class AreaSettings(
    @Column(name = "area_id")
    var areaId: Int? = null,
    var frequencyValue: Int = 0,
    var frequencyUnit: TimeUnit = TimeUnit.DAY,
    var strength: Int = 0,
    var threshhold: Int = 0,
    var isWeatherEnabled: Boolean = false,
    var isIrrigationEnabled: Boolean = false,
    var irrigateNow: Boolean = false
) {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    var id: Int = 0
}