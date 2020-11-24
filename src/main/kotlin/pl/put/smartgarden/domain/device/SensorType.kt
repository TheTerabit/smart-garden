package pl.put.smartgarden.domain.device

enum class SensorType(val unit: String) {
    IRRIGATION("%"),
    ILLUMINANCE("lux"),
    HUMIDITY("%"),
    TEMPERATURE("°C")
}