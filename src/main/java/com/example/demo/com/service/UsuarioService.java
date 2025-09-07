package com.example.demo.com.service;

import java.util.ArrayList;
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

@Data
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonajeRepository personajeRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PersonajeRepository personajeRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.personajeRepository = personajeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public boolean esFavorito(String username, Long favoritoId) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getFavoritos().stream().anyMatch(u -> u.getId().equals(favoritoId));
    }

    @Transactional
    public void agregarFavorito(String username, Long personajeId) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Personaje personaje = personajeRepository.findById(personajeId)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));

        usuario.getFavoritos().add(personaje);
    }

    @Transactional
    public void eliminarFavorito(String username, Long personajeId) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.getFavoritos().removeIf(p -> p.getId().equals(personajeId));
    }

    public Optional<Usuario> buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public Long getIdByUsername(String username) {
        return usuarioRepository.getIdByUsername(username);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public List<Personaje> obtenerFavoritos(String username) {
        Usuario usuario = usuarioRepository.findByUsernameWithFavoritos(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new ArrayList<>(usuario.getFavoritos());
    }

    /**
     * Nuevo método: verifica si un usuario existe por su username.
     *
     * @param username Nombre de usuario
     * @return true si el usuario existe, false si no
     */
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

}
