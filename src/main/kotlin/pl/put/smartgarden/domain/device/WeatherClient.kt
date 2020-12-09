package pl.put.smartgarden.domain.device

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Instant

@Service
class WeatherClient {

    val restTemplate = RestTemplate()

    fun getForecast(latitude: Double, longitude: Double): Forecast {
        val apiForecasts = sendRequest(latitude, longitude)
        val forecastExpiresAt = calculateForecastExpirationTime()
        val isGoingToRain = isGoingToRain(apiForecasts, forecastExpiresAt)
        return Forecast(forecastExpiresAt, isGoingToRain)
    }

    private fun sendRequest(latitude: Double, longitude: Double): ArrayList<ApiForecast> {
        val url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=4aefe593e29e3bb1224da04a5fef6b30"
        val response = restTemplate.getForEntity(url, ApiResponse::class.java)
        return response.body?.list!!
    }

    private fun calculateForecastExpirationTime(): Instant =
        Instant.now().plusSeconds(60 * 90)

    private fun isGoingToRain(apiForecasts: ArrayList<ApiForecast>, forecastExpiresAt: Instant): Boolean {
        if (apiForecasts[0].dt > forecastExpiresAt.epochSecond) {
            apiForecasts[0].rain ?: return false
        } else {
            apiForecasts[0].rain ?: apiForecasts[1].rain ?: return false
        }
        return true
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    val list: ArrayList<ApiForecast>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiForecast(
    val dt: Long,
    val rain: ApiRain?
)

@JsonIgnoreProperties(ignoreUnknown = true)
class ApiRain

data class Forecast(
    val byWhen: Instant,
    val isGoingToRain: Boolean
)
