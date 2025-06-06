package com.tripbook.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransportationApplication

fun main(args: Array<String>) {
    runApplication<TransportationApplication>(*args)
} 