package com.iotabuild.campuscircle.Connections.aws_s3

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class AwsConfig {

    @Bean
    fun s3Presigner(): S3Presigner {
        val accessKey = "AKIASGI2PLFWRLQWX47N"
        val secretKey = "QHY0y7wCzBi0RlO5RTpvflLtwF0AsneMX+CxcviN"
        val region = Region.AP_SOUTH_1  // change if your bucket is elsewhere

        return S3Presigner.builder()
            .region(region)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build()
    }
}
