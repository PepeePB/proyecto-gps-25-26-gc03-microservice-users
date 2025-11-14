package com.musicfly.backend.models;

import com.musicfly.backend.models.DAO.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Objects;

/**
 * Esta clase representa un identificador único para los comentarios asociados a elementos "comentables".
 * Contiene referencias tanto al {@link User} que realiza el comentario como al {@code Element} que está siendo comentado.
 *
 * @param <Element> Tipo genérico que debe extender la interfaz {@link Commentable}. Representa el tipo de
 *                  elemento que será comentado (por ejemplo, producto, canción, etc.).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CommentId<Element extends Commentable> implements Serializable {

    /**
     * El usuario que realiza el comentario.
     * Se establece una relación Many-to-One con la entidad {@link User}.
     * La relación está definida para eliminar en cascada los comentarios asociados si se elimina el usuario.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(id)"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public User user;

    /**
     * El elemento comentable que recibe el comentario.
     * Se establece una relación Many-to-One con la entidad genérica {@code Element},
     * que debe implementar la interfaz {@link Commentable}.
     * La relación está definida para eliminar en cascada los comentarios si se elimina el elemento.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "element_commentable_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public Element element;

    /**
     * Método para comparar dos objetos {@code CommentId} para determinar si son iguales.
     * Dos objetos {@code CommentId} son iguales si el usuario y el elemento comentable son iguales.
     *
     * @param o El objeto a comparar con {@code this}.
     * @return {@code true} si ambos objetos son iguales, de lo contrario {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return false;
    }

    /**
     * Método para generar el código hash del objeto {@code CommentId}.
     * El código hash se genera a partir del usuario y el elemento comentable.
     *
     * @return El código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(user, element);
    }
}
