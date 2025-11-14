package com.musicfly.backend.repositories;

import com.musicfly.backend.models.DAO.SocialMediaLinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para gestionar los enlaces de redes sociales de los artistas.
 * Extiende de JpaRepository, lo que proporciona acceso a métodos estándar CRUD para la entidad SocialMediaLinks.
 */
@Repository
public interface SocialMediaLinksRepository extends JpaRepository<SocialMediaLinks, Long> {
}
