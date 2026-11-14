package com.iotabuild.campuscircle.Common.config.FirebaseAuthService

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val firebaseTokenFilter: FirebaseTokenFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                // Allow all OPTIONS preflight requests
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
                it.requestMatchers("/api/v1/users/view/**").permitAll()
                it.requestMatchers(
                    "/api/v1/users/match-preferences",
                    "/api/v1/users/preferences",
                    "/api/v1/users/goals",
                    "/api/v1/users/profile-goals",
                    "/api/v1/users/collaboration-goals",
                    "/api/v1/users/intents",
                    "/api/v1/users/looking-for"
                ).permitAll()
                it.requestMatchers("/ping", "/ws/**").permitAll()
                
                // Aura endpoints - all require authentication
                it.requestMatchers(
                    "/api/v1/aura/**",
                    "/api/daily-open",
                    "/api/summary",
                    "/api/users/*/aura"
                ).authenticated()
                
                // Everything else requires authentication
                it.anyRequest().authenticated()
            }
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
        )
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
