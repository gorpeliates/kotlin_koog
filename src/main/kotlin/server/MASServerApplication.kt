package server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MASServerApplication

    fun main(args: Array<String>) {
        runApplication<MASServerApplication>(*args)
    }

