package com.example.demo.com.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.com.model.Usuario;
import com.example.demo.com.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import java.util.Collections;

/**
 * Servicio que implementa UserDetailsService para Spring Security.
 *
 * Función principal:
 * - Cargar un usuario por username para la autenticación.
 *
 * Buenas prácticas:
 * 1️⃣ Usar Optional y lanzar UsernameNotFoundException si el usuario no existe.
 * 2️⃣ Construir un objeto UserDetails de Spring Security desde tu entidad Usuario.
 * 3️⃣ Evitar exponer directamente la entidad en la seguridad.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por username.
     *
     * @param username Nombre de usuario
     * @return UserDetails para Spring Security
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // No es necesario este null check porque el Optional ya maneja la ausencia
        // if (usuario == null) { ... }

        // Construir UserDetails para Spring Security
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword()) // Debe estar en hash (ej. BCrypt)
                .authorities(Collections.emptyList()) // Sin roles por ahora, se pueden agregar más tarde
                .build();
    }

    /**
     * Buenas prácticas futuras:
     * 1️⃣ Usar roles y authorities para manejar permisos.
     * 2️⃣ Integrar con JWT o sesiones seguras si la aplicación lo requiere.
     * 3️⃣ Nunca almacenar contraseñas en texto plano: usar BCryptPasswordEncoder.
     * 4️⃣ Evitar cargar colecciones grandes dentro de este método para no impactar la autenticación.
     */
}
