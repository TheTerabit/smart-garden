package pl.put.smartgarden

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartGardenApplication

fun main(args: Array<String>) {
    runApplication<SmartGardenApplication>(*args)
}
