package com.musicfly.backend.exceptions;

import com.musicfly.backend.exceptions.general.*;
import com.musicfly.backend.exceptions.general.*;
import com.musicfly.backend.views.DTO.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;

/**
 * Clase encargada de manejar globalmente las excepciones lanzadas en el sistema.
 * Utiliza anotaciones de Spring para manejar excepciones específicas y devolver respuestas adecuadas
 * con los códigos HTTP correspondientes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja la excepción ContentNotFound. Esta excepción ocurre cuando no se encuentra el contenido solicitado.
     * Se devuelve una respuesta con el código HTTP 404 (Not Found).
     *
     * @param ex La excepción de tipo ContentNotFound.
     * @return Un objeto ResponseEntity con un mensaje de error y el código de estado 404.
     */
    @ExceptionHandler(ContentNotFound.class)
    public ResponseEntity<ErrorResponseDTO> handleContentNotFound(ContentNotFound ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("not_found")
                .message("Content not found: " + ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja la excepción ProcessingException. Esta excepción ocurre cuando hay un error al procesar una solicitud.
     * Se devuelve una respuesta con el código HTTP 500 (Internal Server Error).
     *
     * @param ex La excepción de tipo ProcessingException.
     * @return Una respuesta con el mensaje de error y el código de estado 500.
     */
    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handleProcessingError(ProcessingException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("internal_error")
                .message("Internal Server Error: " + ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja la excepción NullValueException. Esta excepción ocurre cuando un valor requerido es nulo.
     * Se devuelve una respuesta con el código HTTP 400 (Bad Request).
     *
     * @param ex La excepción de tipo NullValueException.
     * @return Una respuesta con el mensaje de error y el código de estado 400.
     */
    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ErrorResponseDTO> handleNullValueException(NullValueException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("bad_request")
                .message("Bad Request: " + ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la excepción BadContentException. Esta excepción ocurre cuando el contenido de la solicitud es inválido.
     * Se devuelve una respuesta con el código HTTP 400 (Bad Request).
     *
     * @param ex La excepción de tipo BadContentException.
     * @return Una respuesta con el mensaje de error y el código de estado 400.
     */
    @ExceptionHandler(BadContentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadContentException(BadContentException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("bad_request")
                .message("Bad Request: " + ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la excepción UnauthorizedException. Esta excepción ocurre cuando el usuario no está autorizado.
     * Se devuelve una respuesta con el código HTTP 401 (Unauthorized).
     *
     * @param ex La excepción de tipo UnauthorizedException.
     * @return Una respuesta con el mensaje de error y el código de estado 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("client_unauthorized")
                .message("Unauthorized: " + ex.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja la excepción MissingServletRequestParameterException. Esta excepción ocurre cuando falta un parámetro obligatorio
     * en la solicitud HTTP.
     * Se devuelve una respuesta con el código HTTP 400 (Bad Request).
     *
     * @param ex La excepción de tipo MissingServletRequestParameterException.
     * @return Una respuesta con un mensaje indicando el parámetro faltante y el código de estado 400.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("missing_parameter")
                .message("Missing required parameter: " + ex.getParameterName())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la excepción MissingServletRequestPartException. Esta excepción ocurre cuando falta una parte de la solicitud multipart.
     * Se devuelve una respuesta con el código HTTP 400 (Bad Request).
     *
     * @param ex La excepción de tipo MissingServletRequestPartException.
     * @return Una respuesta con un mensaje indicando la parte faltante y el código de estado 400.
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleMissingPart(MissingServletRequestParameterException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("missing_part")
                .message("Missing required part: " + ex.getParameterName())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la excepción IllegalArgumentException. Esta excepción ocurre cuando se pasa un argumento ilegal o incorrecto.
     * Se devuelve una respuesta con el código HTTP 400 (Bad Request).
     *
     * @param ex La excepción de tipo IllegalArgumentException.
     * @return Una respuesta con el mensaje de error y el código de estado 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("bad_request")
                .message("Bad request for illegal argument: " + ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la excepción EntityNotFoundException. Esta excepción ocurre cuando no se encuentra una entidad en la base de datos.
     * Se devuelve una respuesta con el código HTTP 404 (Not Found).
     *
     * @param ex La excepción de tipo EntityNotFoundException.
     * @return Un objeto ResponseEntity con un mensaje de error y el código de estado 404.
     */
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(jakarta.persistence.EntityNotFoundException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("entity_not_found")
                .message("Entity not found")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja todas las excepciones generales no tratadas por otros gestores.
     * Se devuelve una respuesta con el código HTTP 500 (Internal Server Error).
     *
     * @param ex    La excepción general.
     * @param model El modelo donde se puede agregar información adicional para una respuesta de error en una vista.
     * @return Una vista de error y el código de estado 500.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("UNHANDED ERROR AT {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("internal_server_error")
                .message("Something went wrong")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}