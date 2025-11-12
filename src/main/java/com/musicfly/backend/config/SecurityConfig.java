package com.musicfly.backend.config;

import com.musicfly.backend.jwt.JwtAuthenticationFilter;
import com.musicfly.backend.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApplicationProperties applicationProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        if (applicationProperties.isOpenDoors()){
            System.err.println("Open Doors = Security Disabled");
            return http
                    .securityMatcher("/api/**")
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll()
                    )
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(AbstractHttpConfigurer::disable)
                    .build();
        }else{
            System.err.println("No Open Doors = Security Enabled");
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(authRequest ->
                            authRequest
                                    // Rutas permitidas y el resto necesitan el JWT
                                    .requestMatchers("/access/**").permitAll()
                                    .requestMatchers("/login/**").permitAll()
                                    .requestMatchers("/register/**").permitAll()
                                    .requestMatchers("/core/views/**").permitAll()
                                    .requestMatchers("/oauth2callback/**").permitAll()
                                    .requestMatchers("/swagger-ui.html/**").permitAll()
                                    .requestMatchers("/v3/api-docs/**").permitAll()
                                    .requestMatchers(request -> request.getRequestURI().contains(".*public.*")).permitAll()
                                    .anyRequest().authenticated()
                    )
                    .oauth2Login(Customizer.withDefaults())
                    .oauth2ResourceServer((oauth2) -> oauth2
                            .jwt(Customizer.withDefaults())) // OAuth2 utilizara JWT para la autenticacion
                    .sessionManagement(sessionManager ->
                            sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ) // No utilizar politicas de sesiones
                    .authenticationProvider(authProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }
    }

    /**
     * Configuración global de CORS.
     * <p>
     * Este método configura las reglas CORS para permitir que las solicitudes desde un origen específico
     * (en este caso, "http://localhost:4200") puedan acceder a los recursos de la API bajo "/api/**".
     * </p>
     *
     * @param registry El objeto {@link CorsRegistry} utilizado para registrar las configuraciones de CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedOrigins("http://localhost:43495")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
