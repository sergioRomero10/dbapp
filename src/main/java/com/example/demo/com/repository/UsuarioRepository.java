package com.example.demo.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.com.model.Usuario;

/**
 * Repositorio para la entidad Usuario.
 * 
 * Buenas prácticas aplicadas:
 * - Extiende JpaRepository para heredar métodos CRUD básicos:
 *      - findAll(), findById(), save(), delete(), existsById(), etc.
 * - Uso de Optional para evitar NullPointerException al buscar por username.
 * - Consulta personalizada con @Query para evitar problemas de LazyInitializationException o ConcurrentModificationException.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Buscar un usuario por su username.
     * 
     * Retorna Optional para manejar el caso en que no exista el usuario.
     * Colecciones lazy (como 'favoritos') no se cargan automáticamente.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Buscar un usuario por username **cargando también los favoritos**.
     * 
     * - Usa JOIN FETCH para inicializar la colección 'favoritos'.
     * - Evita ConcurrentModificationException al añadir/quitar favoritos en el Service.
     */
    @Query("""
           select u
           from Usuario u
           left join fetch u.favoritos
           where u.username = :username
           """)
    Optional<Usuario> findByUsernameWithFavoritos(@Param("username") String username);

    /**
     * Obtener solo el ID del usuario por username.
     * 
     * - Utiliza findByUsername() y extrae el ID.
     * - Lanza excepción si el usuario no existe.
     */
    default Long getIdByUsername(String username) {
        return findByUsername(username)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }
    boolean existsByUsername(String username);

    /**
     * Buenas prácticas futuras:
     * 1️⃣ Considerar agregar índices únicos en username para mejorar consultas.
     * 2️⃣ Usar @EntityGraph como alternativa a JOIN FETCH para cargar colecciones específicas.
     * 3️⃣ Evitar cargar colecciones muy grandes si no se necesitan, para mejorar rendimiento.
     */
}
