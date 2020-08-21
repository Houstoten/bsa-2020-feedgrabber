package com.feed_grabber.event_processor.fileStorage


import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime
import java.util.*

@Service
class AmazonS3ClientService(private val s3Client: S3Client) {

    @Value("\${amazon.bucketName}")
    private lateinit var BUCKET_NAME: String

    @Value("\${amazon.endpointUrl}")
    private lateinit var ENDPOINT: String

    fun uploadReport(resource: Resource): String {
        return putObject(resource, "reports/")
    }

    fun putObject(resource: Resource, prefix: String): String {
        val key = prefix + LocalDateTime.now() + "-" + resource.filename?.replace(" ","_") ?: UUID.randomUUID().toString()
        val request = PutObjectRequest.builder().bucket(BUCKET_NAME).key(key).acl(ObjectCannedACL.PUBLIC_READ).build()
        val requestBody = RequestBody.fromInputStream(resource.inputStream, resource.contentLength())
        s3Client.putObject(request, requestBody)
        return "$ENDPOINT$BUCKET_NAME/$key"
    }

}