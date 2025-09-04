package com.example.demo.com.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.model.Usuario;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.Data;

/**
 * Servicio que contiene la lógica de negocio relacionada con Usuario.
 * 
 * Buenas prácticas aplicadas:
 * - Separación de responsabilidades: el Service maneja la lógica de negocio y las operaciones sobre la BD.
 * - Uso de @Transactional para operaciones que modifican colecciones de Hibernate.
 * - Manejo de Optional y RuntimeException para controlar errores de manera clara.
 */
@Data
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonajeRepository personajeRepository;
    private final PasswordEncoder passwordEncoder; // 
    public UsuarioService(UsuarioRepository usuarioRepository, PersonajeRepository personajeRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.personajeRepository = personajeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registrar un nuevo usuario.
     * 
     * @param usuario Usuario a registrar
     * @return Usuario guardado en la BD
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Aquí es recomendable hashear la contraseña antes de guardar
    	usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Verifica si un personaje es favorito de un usuario.
     * 
     * @param username Nombre de usuario
     * @param favoritoId ID del personaje
     * @return true si el personaje está en favoritos
     */
    public boolean esFavorito(String username, Long favoritoId) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getFavoritos().stream().anyMatch(u -> u.getId().equals(favoritoId));
    }

    /**
     * Agregar un personaje a los favoritos de un usuario.
     * 
     * - Se usa @Transactional para que Hibernate gestione automáticamente la colección y la sesión.
     * - findByUsernameWithFavoritos evita ConcurrentModificationException cargando la colección completa.
     */
    @Transactional
    public void agregarFavorito(String username, Long personajeId) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Personaje personaje = personajeRepository.findById(personajeId)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));

        usuario.getFavoritos().add(personaje); // Hibernate sincroniza la colección con la BD al finalizar la transacción
    }

    /**
     * Eliminar un personaje de los favoritos del usuario.
     * 
     * - Se usa removeIf para evitar problemas de iteración sobre la colección.
     */
    @Transactional
    public void eliminarFavorito(String username, Long personajeId) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.getFavoritos().removeIf(p -> p.getId().equals(personajeId));
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<Usuario> buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    /**
     * Obtener solo el ID del usuario por username
     */
    public Long getIdByUsername(String username) {
        return usuarioRepository.getIdByUsername(username);
    }

    /**
     * Buscar usuario por username
     */
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }
    
    public List<Personaje> obtenerFavoritos(String username) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Convertimos Set a List explícitamente
        return new ArrayList<>(usuario.getFavoritos());
    }
    /**
     * Buenas prácticas futuras:
     * 1️⃣ Manejar excepciones personalizadas en lugar de RuntimeException para más claridad.
     * 2️⃣ Aplicar hashing a la contraseña antes de guardar.
     * 3️⃣ Validaciones de datos con DTOs y @Valid.
     * 4️⃣ Considerar uso de Optional en métodos que modifican colecciones para más seguridad.
     * 5️⃣ Evitar cargar colecciones grandes si no es necesario para mejorar rendimiento.
     */
}
