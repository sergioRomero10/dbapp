package com.example.demo.com.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.model.Usuario;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PersonajeRepository personajeRepository;

	@Mock
	private PasswordEncoder passwordEncoder;


	@InjectMocks
	private UsuarioService usuarioService;
	
	@Test
	void esFavorito_devuelveTrue_siElPersonajeEstaEnFavoritos() {
	    // ARRANGE
	    Personaje personaje = new Personaje(1L, "Goku", "60000000", "90000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);

	    Usuario usuario = new Usuario();
	    usuario.setUsername("sergio");
	    usuario.setFavoritos(new HashSet<>(Set.of(personaje)));

	    when(usuarioRepository.findByUsername("sergio")).thenReturn(Optional.of(usuario));

	    // ACT
	    boolean resultado = usuarioService.esFavorito("sergio", 1L);

	    // ASSERT
	    assertTrue(resultado);
	}
	
	@Test
	void esFavorito_devuelveFalse_siElPersonajeNoEstaEnFavoritos() {
	    // ARRANGE
	    Personaje personaje = new Personaje(5L, "Goku", "60000000", "90000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);

	    Usuario usuario = new Usuario();
	    usuario.setUsername("sergio");
	    usuario.setFavoritos(new HashSet<>(Set.of(personaje)));

	    when(usuarioRepository.findByUsername("sergio")).thenReturn(Optional.of(usuario));

	    // ACT
	    boolean resultado = usuarioService.esFavorito("sergio", 1L);

	    // ASSERT
	    assertFalse(resultado);
	}
	
	
	@Test
	void esFavorito_lanzaExcepcion_siUsuarioNoExiste() {
	    // ARRANGE
	    when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

	    // ACT + ASSERT (van juntos porque estamos comprobando que algo lance una excepción)
	    assertThrows(RuntimeException.class, () ->
	        usuarioService.esFavorito("noexiste", 1L));
	}
	
	@Test
	void registrarUsuario_ciframContrasenaYGuardaElUsuario() {
	    // ARRANGE
	    Usuario usuario = new Usuario();
	    usuario.setUsername("Sergio");
	    usuario.setPassword("Pruebacontra");

	    when(passwordEncoder.encode("Pruebacontra")).thenReturn("hash_falso_123");
	    when(usuarioRepository.save(usuario)).thenReturn(usuario);

	    // ACT
	    Usuario resultado = usuarioService.registrarUsuario(usuario);

	    // ASSERT
	    verify(passwordEncoder).encode("Pruebacontra");
	    verify(usuarioRepository).save(usuario);
	    assertEquals("hash_falso_123", usuario.getPassword());
	    assertEquals(usuario, resultado);
	}
	
	
}
