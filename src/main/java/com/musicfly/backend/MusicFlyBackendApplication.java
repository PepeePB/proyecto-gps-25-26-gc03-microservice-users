package com.musicfly.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableEurekaServer  // Habilita el servidor Eureka (Registrador de microservicios)
//@EnableConfigurationProperties(RsaKeysConfig.class) // Detecta la clase para poder acceder a properties
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
