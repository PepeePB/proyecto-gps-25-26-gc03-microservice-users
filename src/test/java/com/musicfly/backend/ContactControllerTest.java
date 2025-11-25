package com.musicfly.backend;

import com.musicfly.backend.controllers.ContactController;
import com.musicfly.backend.services.ContactService;
import com.musicfly.backend.views.DTO.ContactDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ContactControllerTest {

    private ContactService contactService;
    private ContactController contactController;

    @BeforeEach
    void setUp() {
        contactService = mock(ContactService.class);
        contactController = new ContactController(contactService);
    }

    @Test
    void testSendContactMessage_success() {
        // Preparar DTO de prueba
        ContactDTO dto = ContactDTO.builder()
                .name("Juan Pérez")
                .email("juan@example.com")
                .message("Hola, quiero más información.")
                .build();

        // Llamada al controller
        String response = contactController.sendContactMessage(dto);

        // Verifica que el servicio haya sido llamado correctamente
        verify(contactService, times(1)).processContactMessage(dto);

        // Verifica el valor de retorno
        assertEquals("Mensaje recibido correctamente", response);
    }

    @Test
    void testSendContactMessage_nullDTO() {
        // Si enviamos null, depende de tu controller si quieres manejarlo o dejar que falle
        try {
            contactController.sendContactMessage(null);
        } catch (Exception e) {
            // Podemos verificar que lance NullPointerException
            assertEquals(NullPointerException.class, e.getClass());
        }

        // Asegurarnos de que el servicio nunca fue llamado
        verify(contactService, never()).processContactMessage(any());
    }
}
