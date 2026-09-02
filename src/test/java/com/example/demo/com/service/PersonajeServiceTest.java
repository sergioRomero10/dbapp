package com.example.demo.com.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class PersonajeServiceTest {
	
	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PersonajeRepository personajeRepository;



	@InjectMocks
	private PersonajeService personajeService;
	
	
	@Test
	void buscarPorNombre_encuentrapersonaje () {
		Personaje goku = new Personaje(1L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje vegeta = new Personaje(2L, "Vegetta", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje gohan = new Personaje(3L, "Gohan", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		
		when(personajeRepository.findAll()).thenReturn(List.of(goku,vegeta,gohan));
		List<Personaje> resultado = personajeService.buscarPorNombre("go");
		
		 assertEquals(2, resultado.size());
		 assertTrue(resultado.contains(goku));
		 assertTrue(resultado.contains(gohan));
		 assertFalse(resultado.contains(vegeta));
		
	}
	
	
	@Test
	void buscarPorNombre_noEncuentrapersonaje () {
		Personaje goku = new Personaje(1L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje vegeta = new Personaje(2L, "Vegetta", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje gohan = new Personaje(3L, "Gohan", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		
		when(personajeRepository.findAll()).thenReturn(List.of(goku,vegeta,gohan));
		List<Personaje> resultado = personajeService.buscarPorNombre("yyi");
		
		 assertEquals(0, resultado.size());
		 assertFalse(resultado.contains(goku));
		 assertFalse(resultado.contains(gohan));
		 assertFalse(resultado.contains(vegeta));
		
	}
	
	
	@Test
	void buscarPorRaza_encuentraPersonaje () {
		Personaje goku = new Personaje(1L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje vegeta = new Personaje(2L, "Vegetta", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje piccolo = new Personaje(3L, "Piccolo", "60000000", "90000000",
				"Namekian", "Male", "desc", "img.png", "Z Fighter", null);
		
		when(personajeRepository.findAll()).thenReturn(List.of(goku,vegeta,piccolo));
		List<Personaje> resultado = personajeService.buscarPorRaza("Saiyan");
		
		 assertEquals(2, resultado.size());
		 assertTrue(resultado.contains(goku));
		 assertFalse(resultado.contains(piccolo));
		 assertTrue(resultado.contains(vegeta));
		
	}
	
	@Test
	void buscarPorRaza_noEncuentraPersonaje () {
		Personaje goku = new Personaje(1L, "Goku", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje vegeta = new Personaje(2L, "Vegetta", "60000000", "90000000",
				"Saiyan", "Male", "desc", "img.png", "Z Fighter", null);
		Personaje piccolo = new Personaje(3L, "Piccolo", "60000000", "90000000",
				"Namekian", "Male", "desc", "img.png", "Z Fighter", null);
		
		when(personajeRepository.findAll()).thenReturn(List.of(goku,vegeta,piccolo));
		List<Personaje> resultado = personajeService.buscarPorRaza("Frieza Race");
		
		 assertEquals(0, resultado.size());
		 assertFalse(resultado.contains(goku));
		 assertFalse(resultado.contains(piccolo));
		 assertFalse(resultado.contains(vegeta));
		
	}
}
