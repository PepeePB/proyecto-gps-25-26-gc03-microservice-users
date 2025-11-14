package com.musicfly.backend.services;

import com.musicfly.backend.views.DTO.ContactDTO;
import org.springframework.stereotype.Service;

/**
 * Servicio para procesar los mensajes de contacto.
 * Este servicio simula el procesamiento de mensajes de contacto, como si se enviara un correo electrónico.
 */
@Service
public class ContactService {

    /**
     * Constructor vacío ya que no necesitamos un servicio de envío de correo real.
     */
    public ContactService() {
        // No se necesita inicializar un emailService real
    }

    /**
     * Lógica para procesar el mensaje de contacto recibido y simular el envío de un correo electrónico.
     *
     * @param contactDTO El DTO que contiene los detalles del mensaje de contacto.
     *                  Este DTO incluye el nombre del remitente, su correo electrónico y el mensaje.
     *
     * El mensaje se imprime en la consola como una simulación del procesamiento.
     * También se simula el envío del correo electrónico a una dirección predefinida.
     */
    public void processContactMessage(ContactDTO contactDTO) {
        // Imprimir el mensaje de contacto en la consola para simular el procesamiento
        System.out.println("Mensaje de contacto recibido:");
        System.out.println("Nombre: " + contactDTO.getName());
        System.out.println("Correo: " + contactDTO.getEmail());
        System.out.println("Mensaje: " + contactDTO.getMessage());

        // Simular la creación del correo electrónico con la información del contacto
        String subject = "Nuevo mensaje de contacto de " + contactDTO.getName();
        String text = "Correo: " + contactDTO.getEmail() + "\n\nMensaje:\n" + contactDTO.getMessage();

        // Imprimir en consola como si fuera un correo electrónico enviado
        System.out.println("\n*** Simulación de envío de correo ***");
        System.out.println("Enviando correo a: undersounds2025@gmail.com");
        System.out.println("Asunto: " + subject);
        System.out.println("Contenido del mensaje: \n" + text);
        System.out.println("*** Fin de simulación ***");
    }
}
