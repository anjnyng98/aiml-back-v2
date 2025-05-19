package org.aiml.api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
  private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf() { it.disable() }
      .authorizeHttpRequests { requests ->
        requests
          .requestMatchers("/api/auth/**", "/api/project/search", "/test/**").permitAll()
          .anyRequest().authenticated() // 그 외에는 인증이 필요
      }
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // JWT 필터 추가
    return http.build()
  }

  @Bean
  fun authenticationManager(
    authConfig: AuthenticationConfiguration
  ): AuthenticationManager {
    return authConfig.authenticationManager
  }
}
