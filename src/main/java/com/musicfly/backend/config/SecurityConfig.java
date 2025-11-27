package com.musicfly.backend.config;

import com.musicfly.backend.jwt.JwtAuthenticationFilter;
import com.musicfly.backend.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApplicationProperties applicationProperties;

    // =======================
    // SecurityChain para Swagger y Eureka
    // =======================
    @Bean
    public SecurityFilterChain swaggerAndEurekaSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/eureka",
                        "/eureka/",
                        "/eureka/**"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/eureka/**")) // POST/PUT de clientes
                .headers(headers -> headers.frameOptions().disable())     // Eureka dashboard usa iframe
                .cors(Customizer.withDefaults());

        return http.build();
    }

    // =======================
    // SecurityChain principal para la API
    // =======================
    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        // Open Doors habilitado: API pública
        if (applicationProperties.isOpenDoors()) {
            return http
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> {}) // habilitar CORS
                    .build();
        }

        // API protegida con JWT y OAuth2
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {}) // habilitar CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/access/**",
                                "/login/**",
                                "/register/**",
                                "/core/views/**",
                                "/oauth2callback/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // =======================
    // Configuración CORS global
    // =======================
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://localhost:43495",
                        "http://localhost:5173",
                        "http://0.0.0.0:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
