package com.musicfly.backend.models;

import java.io.Serializable;

/**
 * Esta interfaz permite definir que una clase es comentable.
 * Las clases que implementen esta interfaz pueden tener comentarios asociados,
 * ya que se garantiza que tendrán un método `getId()` que será utilizado para asociar los comentarios.
 *
 * Las clases que implementen esta interfaz deben tener un identificador único (ID)
 * que puede ser usado para asociar los comentarios a la entidad correspondiente.
 */
public interface Commentable {

    /**
     * Método que retorna el identificador único de la entidad.
     *
     * @return El ID único de la entidad comentable.
     */
    public Long getId();
}
