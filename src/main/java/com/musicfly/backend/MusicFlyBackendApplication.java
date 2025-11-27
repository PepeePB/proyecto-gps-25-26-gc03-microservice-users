package com.musicfly.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDiscoveryClient  // Habilita el servidor Eureka (Registrador de microservicios)
public class MusicFlyBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(MusicFlyBackendApplication.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(MusicFlyBackendApplication.class, args);
        long end = System.currentTimeMillis();
        logger.info("Application started in {} seconds",
                (end - start) / 1000.0);
    }

}
