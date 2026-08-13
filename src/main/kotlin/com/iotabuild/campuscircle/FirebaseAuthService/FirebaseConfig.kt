package com.iotabuild.campuscircle.FirebaseAuthService

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.InputStream


@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        if (FirebaseApp.getApps().isNotEmpty()) return FirebaseApp.getInstance()
        
        val resource = ClassPathResource("firebase/firebase-service.json")
        val serviceAccount: InputStream = resource.inputStream
        
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth {
        return FirebaseAuth.getInstance(firebaseApp)
    }

    @Bean
    fun firebaseMessaging(
        firebaseApp: FirebaseApp
    ): FirebaseMessaging {

        return FirebaseMessaging.getInstance(firebaseApp)
    }
}
