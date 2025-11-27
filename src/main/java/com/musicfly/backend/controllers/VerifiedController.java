package com.musicfly.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verified")
@RequiredArgsConstructor
public class VerifiedController {

    /*
    Simplemente sirve para verificar que un usuario tiene un token valido,
    es una forma muy sucia de hacerlo asi, o eso pienso yo
    si devuelve 200 es que puede pasar por el api-gateway
    ya que esta ruta se encuentra protegida por JWT
     */
    @PostMapping
    public ResponseEntity<?> ok(){
        return ResponseEntity.ok().build();
    }
}
