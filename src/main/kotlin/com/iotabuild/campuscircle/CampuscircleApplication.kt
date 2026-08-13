package com.iotabuild.campuscircle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


@EnableAsync
@SpringBootApplication
class CampuscircleApplication

fun main(args: Array<String>) {
	runApplication<CampuscircleApplication>(*args)
}


