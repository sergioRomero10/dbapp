package com.example.demo.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.com.model.Personaje;
import java.util.List;

/**
 * Repositorio para la entidad Personaje.
 * 
 * Buenas prácticas:
 * - Extender JpaRepository para heredar métodos CRUD básicos:
 *      - findAll(), findById(), save(), delete(), existsById(), etc.
 * - Spring Data JPA genera la implementación automáticamente, sin necesidad de escribir SQL.
 * 
 * Mejoras posibles para proyectos más grandes:
 * 1️⃣ Consultas por campos específicos usando convenciones de Spring Data:
 *      - findByNameContainingIgnoreCase(String nombre)
 *      - findByRaceContainingIgnoreCase(String race)
 *    Esto evita traer todos los registros y filtrar en memoria.
 *
 * 2️⃣ @Query personalizado si necesitas SQL complejo.
 * 3️⃣ Paginación y sorting con Pageable si esperas muchos registros.
 */
public interface PersonajeRepository extends JpaRepository<Personaje, Long> {
}
