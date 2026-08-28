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
	
}
