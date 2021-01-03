package pl.put.smartgarden

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["pl.put.smartgarden.domain.device.repository", "pl.put.smartgarden.domain.user.repository"])
@EnableScheduling
class SmartGardenApplication

fun main(args: Array<String>) {
    runApplication<SmartGardenApplication>(*args)
}