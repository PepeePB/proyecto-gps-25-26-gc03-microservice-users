package com.musicfly.backend.properties;



import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/***
 * Vincula elementos del application.properties
 */

@Component
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationProperties {

        @Value("${app.domain}")
        private String domain;
        @Value("${app.domain.frontend}")
        private String domainFrontend;
        @Value("${app.open-doors}")
        private boolean openDoors;
        @Value("${app.rsa.public}")
        private RSAPublicKey publicKey;
        @Value("${app.rsa.private}")
        private RSAPrivateKey privateKey;

}
