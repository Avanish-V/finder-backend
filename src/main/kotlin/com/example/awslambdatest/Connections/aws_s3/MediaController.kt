package com.iotabuild.campuscircle.Connections.aws_s3

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/media")
class MediaController(private val s3Service: S3Service) {

    @GetMapping("/presign")
    fun getPresignedUrl(@RequestParam fileName: String): ResponseEntity<Map<String, String>> {
        val url = s3Service.generatePresignedUrl(fileName, "campuscircle")
        return ResponseEntity.ok(mapOf("url" to url))
    }
}