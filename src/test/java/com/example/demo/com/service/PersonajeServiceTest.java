package com.example.demo.com.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PersonajeServiceTest {
	
	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PersonajeRepository personajeRepository;


	@Mock
	private RestTemplate restTemplate;

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
	
	
	@Test
	void obtenerPersonajes_cargaDesdeAPI_siLaBaseDeDatosEstaVacia() {
	    // ARRANGE

	    // 1. El personaje que "existirá" después de cargar desde la API
	    Personaje personaje = new Personaje(1L, "Goku", "60000000", "90000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);

	    // 2. personajeRepository.findAll() responde distinto según el momento:
	    //    primera llamada -> vacío (aún no hay nada en BD)
	    //    segunda llamada -> ya con el personaje (después de cargarDesdeAPI)
	    when(personajeRepository.findAll())
	        .thenReturn(List.of())
	        .thenReturn(List.of(personaje));

	    // 3. Construimos la respuesta falsa que "devolvería" la API externa
	    Map<String, Object> itemGoku = new HashMap<>();
	    itemGoku.put("id", 1);
	    itemGoku.put("name", "Goku");
	    itemGoku.put("ki", "60000000");
	    itemGoku.put("maxKi", "90000000");
	    itemGoku.put("race", "Saiyan");
	    itemGoku.put("gender", "Male");
	    itemGoku.put("description", "desc");
	    itemGoku.put("image", "img.png");
	    itemGoku.put("affiliation", "Z Fighter");
	    itemGoku.put("deletedAt", null);

	    Map<String, Object> links = new HashMap<>();
	    links.put("next", null); // no hay más páginas

	    Map<String, Object> respuestaAPI = new HashMap<>();
	    respuestaAPI.put("items", List.of(itemGoku));
	    respuestaAPI.put("links", links);

	    // 4. Cuando restTemplate llame a la API (con cualquier URL), responde con nuestro mapa falso
	    when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(respuestaAPI);

	    // ACT
	    List<Personaje> resultado = personajeService.obtenerPersonajes();

	    // ASSERT
	    assertEquals(1, resultado.size());
	    verify(personajeRepository).save(any(Personaje.class)); // se guardó el personaje traído de la API
	    verify(restTemplate).getForObject(anyString(), eq(Map.class)); // se llamó a la API
	}
	
	@Test
	void obtenerPersonajes_noLlamaALaAPI_siLaBaseDeDatosYaTieneDatos() {
	    // ARRANGE
	    Personaje personaje = new Personaje(1L, "Goku", "60000000", "90000000",
	            "Saiyan", "Male", "desc", "img.png", "Z Fighter", null);

	    when(personajeRepository.findAll()).thenReturn(List.of(personaje)); // la BD ya tiene datos

	    // ACT
	    List<Personaje> resultado = personajeService.obtenerPersonajes();

	    // ASSERT
	    assertEquals(1, resultado.size());
	    verify(restTemplate, never()).getForObject(anyString(), eq(Map.class)); // nunca se llamó a la API
	}
}
