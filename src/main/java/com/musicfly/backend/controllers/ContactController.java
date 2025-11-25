package com.musicfly.backend.controllers;

import com.musicfly.backend.views.DTO.ContactDTO;
import com.musicfly.backend.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que maneja las operaciones relacionadas con los mensajes de contacto.
 * Expone un endpoint para recibir los mensajes enviados por los usuarios a través del formulario de contacto.
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * Endpoint para recibir los mensajes de contacto enviados por los usuarios.
     * El mensaje es procesado por el servicio correspondiente y se devuelve una respuesta de éxito.
     *
     * @param contactDTO El objeto que contiene la información del mensaje de contacto.
     * @return Una cadena de texto indicando que el mensaje fue recibido correctamente.
     */
    @PostMapping("/send")
    public String sendContactMessage(@RequestBody ContactDTO contactDTO) {
        if (contactDTO == null) {
            throw new NullPointerException("El DTO no puede ser null");
        }

        contactService.processContactMessage(contactDTO);
        return "Mensaje recibido correctamente";
    }
}
