package com.iotabuild.campuscircle.Common.config.FirebaseAuthService

import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseTokenFilter(
    private val firebaseAuth: FirebaseAuth
) : OncePerRequestFilter() {

    /**
     * Resolves the effective request path, handling the AWS Lambda proxy case where
     * servletPath is an empty string (not null) instead of the actual path.
     */
    private fun resolvePath(request: HttpServletRequest): String {
        val servletPath = request.servletPath
        return if (!servletPath.isNullOrBlank()) servletPath else request.requestURI
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = resolvePath(request)

        if (path == "/ping") {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            try {
                val decodedToken = firebaseAuth.verifyIdToken(token)
                request.setAttribute("firebaseUser", decodedToken)
                val auth = UsernamePasswordAuthenticationToken(decodedToken.uid, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: Exception) {
                println("Firebase Token Error: ${e.message}")
                e.printStackTrace()
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // Always bypass CORS preflight OPTIONS requests
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            return true
        }

        // Use resolvePath() to correctly handle Lambda's empty servletPath
        val path = resolvePath(request)

        return path == "/ping" ||
                path.startsWith("/test/") ||
                path.startsWith("/api/v1/users/view/") ||
                path.startsWith("/api/v1/users/match-preferences") ||
                path.startsWith("/api/v1/users/preferences") ||
                path.startsWith("/api/v1/users/goals") ||
                path.startsWith("/api/v1/users/profile-goals") ||
                path.startsWith("/api/v1/users/collaboration-goals") ||
                path.startsWith("/api/v1/users/intents") ||
                path.startsWith("/api/v1/users/looking-for") ||
                path.startsWith("/ws/")
    }
}

