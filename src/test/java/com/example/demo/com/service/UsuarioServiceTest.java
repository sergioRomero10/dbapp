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

	@Test
	void registrarUsuario_lanzaExcepcion_siLaContrasenaEstaVacia() {
		// ARRANGE
		Usuario usuario = new Usuario();
		usuario.setUsername("Sergio");
		usuario.setPassword("");

		// ACT + ASSERT
		assertThrows(RuntimeException.class, () -> usuarioService.registrarUsuario(usuario));
	}

	@Test
	void agregarFavorito_lanzaExcepcion_siUsuarioNoExiste() {
		// ARRANGE
		when(usuarioRepository.findByUsernameWithFavoritos("Sergio"))
		.thenReturn(Optional.empty());

		// ACT + ASSERT
		assertThrows(RuntimeException.class, () ->
		usuarioService.agregarFavorito("Sergio",1L));
	}

	@Test
	void agregarFavorito_lanzaExcepcion_siPersonajeNoExiste() {
		// ARRANGE
		Usuario usuario = new Usuario();
		usuario.setUsername("Sergio");
		usuario.setPassword("");
		Personaje personaje = new Personaje(5L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		// ARRANGE
		when(usuarioRepository.findByUsernameWithFavoritos(usuario.getUsername()))
		.thenReturn(Optional.of(usuario));
		when(personajeRepository.findById(personaje.getId())).thenReturn(Optional.empty());

		// ACT + ASSERT
		assertThrows(RuntimeException.class, () ->
		usuarioService.agregarFavorito(usuario.getUsername(),personaje.getId()));
	}

	@Test
	void agregarFavorito_guardaFavorito_siUsuarioYPersonajeExisten() {
		// ARRANGE
		Usuario usuario = new Usuario();
		usuario.setUsername("Sergio");
		usuario.setPassword("");
		Personaje personaje = new Personaje(5L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		// ARRANGE
		when(usuarioRepository.findByUsernameWithFavoritos(usuario.getUsername()))
		.thenReturn(Optional.of(usuario));
		when(personajeRepository.findById(personaje.getId())).thenReturn(Optional.of(personaje));

		// ACT + ASSERT
		usuarioService.agregarFavorito(usuario.getUsername(),personaje.getId());
		assertTrue(usuario.getFavoritos().contains(personaje));
	}

	@Test 
	void eliminarFavorito_lanzaExcepcion_SiUsuarioNoExiste() {
		// ARRANGE
		when(usuarioRepository.findByUsernameWithFavoritos("Sergio"))
		.thenReturn(Optional.empty());

		// ACT + ASSERT
		assertThrows(RuntimeException.class, () ->
		usuarioService.eliminarFavorito("Sergio",1L));
	}


	@Test 
	void eliminarFavorito_eliminaFavorito_SiUsuarioYPersonajeExisten() {
		// ARRANGE
		Usuario usuario = new Usuario();
		usuario.setUsername("Sergio");
		usuario.setPassword("");
		
		Personaje personaje = new Personaje(5L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje personajeEliminar = new Personaje(51L, "Vegetta", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		usuario.setFavoritos(new HashSet<>(Set.of(personaje)));
		// ARRANGE
		when(usuarioRepository.findByUsernameWithFavoritos(usuario.getUsername()))
		.thenReturn(Optional.of(usuario));
		// ACT + ASSERT
		
		usuarioService.eliminarFavorito(usuario.getUsername(),personaje.getId());
		assertFalse(usuario.getFavoritos().contains(personaje));
	}
	
	@Test
	void eliminarFavorito_noHaceNada_siPersonajeNoEstaEnFavoritos() {
	    // ARRANGE
	    Usuario usuario = new Usuario();
	    usuario.setUsername("Sergio");
	    usuario.setPassword("");

	    Personaje personajeEnFavoritos = new Personaje(5L, "Goku", "60000000", "90000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
	    Personaje personajeAEliminar = new Personaje(99L, "Vegeta", "70000000", "95000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);

	    usuario.setFavoritos(new HashSet<>(Set.of(personajeEnFavoritos)));

	    when(usuarioRepository.findByUsernameWithFavoritos(usuario.getUsername()))
	        .thenReturn(Optional.of(usuario));

	    // ACT
	    usuarioService.eliminarFavorito(usuario.getUsername(), personajeAEliminar.getId());

	    // ASSERT
	    assertEquals(1, usuario.getFavoritos().size());
	    assertTrue(usuario.getFavoritos().contains(personajeEnFavoritos));
	}
	
	@Test
	void findByUsername_lanzaExcepcion_siUsuarioNoExiste() {
	    when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

	    assertThrows(RuntimeException.class, () ->
	        usuarioService.findByUsername("noexiste"));
	}
}
