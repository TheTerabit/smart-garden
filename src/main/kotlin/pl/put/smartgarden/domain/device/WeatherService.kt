package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.WeatherRepository
import java.time.Instant

@Service
class WeatherService(
    val weatherRepository: WeatherRepository,
    val weatherClient: WeatherClient
) {
    fun isGoingToRain(device: Device): Boolean {
        val weatherFromDb: Weather? = weatherRepository.findFirstByDeviceId(device.id)

        if (isWeatherForecastUpToDate(weatherFromDb)) {
            return weatherFromDb!!.isGoingToRain
        } else {
            val weather = getWeatherForecast(weatherFromDb, device)
            weatherRepository.save(weather)
            return weather.isGoingToRain
        }
    }

    private fun isWeatherForecastUpToDate(weatherFromDb: Weather?): Boolean =
        weatherFromDb != null && Instant.now().isBefore(weatherFromDb.byWhen)

    private fun getWeatherForecast(weatherFromDb: Weather?, device: Device): Weather {
        val forecast = weatherClient.getForecast(device.latitude, device. longitude)
        if (weatherFromDb != null) {
            weatherFromDb.isGoingToRain = forecast.isGoingToRain
            weatherFromDb.byWhen = forecast.byWhen
            return weatherFromDb
        } else {
            return Weather(device.id, forecast.byWhen, forecast.isGoingToRain)
        }
    }
}
