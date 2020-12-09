package pl.put.smartgarden.domain.device

enum class TimeUnit(val inSeconds: Int) {
    MINUTE(60),
    HOUR(60 * 60),
    DAY(60 * 60 * 24)
}