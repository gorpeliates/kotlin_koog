package server

import kotlinx.serialization.Contextual
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@ComponentScan(basePackages = ["roles","server","configurations"])
class MASServerApplication

    fun main(args: Array<String>) {
        runApplication<MASServerApplication>(*args)
    }



