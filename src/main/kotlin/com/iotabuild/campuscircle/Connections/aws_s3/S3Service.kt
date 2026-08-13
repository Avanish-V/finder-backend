package com.iotabuild.campuscircle.Connections.aws_s3

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.time.Duration

@Service
class S3Service(private val s3Presigner: S3Presigner) {

    fun generatePresignedUrl(fileName: String, bucketName: String, durationMinutes: Long = 10): String {
        // Build a minimal PutObjectRequest (no ACL, no content-type)
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build()

        // Build the presign request
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(durationMinutes))
            .putObjectRequest(putObjectRequest)
            .build()

        // Generate presigned URL
        val presignedRequest = s3Presigner.presignPutObject(presignRequest)
        return presignedRequest.url().toString()
    }
}

