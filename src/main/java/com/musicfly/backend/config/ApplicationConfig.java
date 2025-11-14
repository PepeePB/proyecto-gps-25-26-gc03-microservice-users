package com.musicfly.backend.config;

import com.musicfly.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /***
     * Obtiene el authentication manager de la configuracion pasada en formato instancia
     * AuthenticationConfiguration
     * @param config
     * @return AuthenticationManager from config
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticatorManager(AuthenticationConfiguration config)
        throws Exception {
            return config.getAuthenticationManager();
    }

    /***
     * Proveedor de la autentificacion, nos permite componer los elementos que serán usados para autenticar
     * a un usuario
     * @return Instancia de AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /***
     * Servicio que busca un usuario en la base de datos, lanza una excepcion si este no ha sido encontrado
     * @return Instancia de UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailService(){
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /***
     * Implementacion de PasswordEncoder seleccionada para el cifrado de la contraseña (usado para almacenar y recuperar
     * la contraseña de la bd)
     * @return Instancia de BCryptPassword Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
