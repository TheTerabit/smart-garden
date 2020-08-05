package pl.put.smartgerden

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartGerdenApplication

fun main(args: Array<String>) {
    runApplication<SmartGerdenApplication>(*args)
}
