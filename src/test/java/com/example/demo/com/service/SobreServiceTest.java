package com.example.demo.com.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.model.Rareza;
import com.example.demo.com.model.ResultadoSobre;
import com.example.demo.com.model.Usuario;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class SobreServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PersonajeRepository personajeRepository;

	@Mock
	private RandomGenerator random;

	@InjectMocks
	private SobreService sobreService;

	private Usuario usuario;

	/** 10 personajes: el de id 10 es el más poderoso (legendario), los de id 1-4 son comunes. */
	private List<Personaje> personajes;

	@BeforeEach
	void setUp() {
		usuario = new Usuario();
		usuario.setUsername("goku");
		usuario.setMonedas(1000);

		personajes = new ArrayList<>();
		for (long id = 1; id <= 10; id++) {
			personajes.add(new Personaje(id, "P" + id, "0", id + " Billion", "Saiyan", "Male", "", "", "", null));
		}
	}

	@Test
	void abrirSobre_conMonedasSuficientes_cobraPrecioYAnadePersonajeNuevoALaColeccion() {
		when(usuarioRepository.findByUsernameWithColeccion("goku")).thenReturn(Optional.of(usuario));
		when(personajeRepository.findAll()).thenReturn(personajes);
		when(random.nextInt(anyInt())).thenReturn(0); // primera rareza (legendaria) y primer candidato

		ResultadoSobre resultado = sobreService.abrirSobre("goku");

		assertEquals(10L, resultado.personaje().getId());
		assertEquals(Rareza.LEGENDARIA, resultado.rareza());
		assertFalse(resultado.repetido());
		assertEquals(900, usuario.getMonedas());
		assertTrue(usuario.getColeccion().contains(resultado.personaje()));
	}

	@Test
	void abrirSobre_personajeRepetido_devuelveReembolsoSegunRareza() {
		usuario.getColeccion().add(personajes.get(9)); // ya tiene al legendario
		when(usuarioRepository.findByUsernameWithColeccion("goku")).thenReturn(Optional.of(usuario));
		when(personajeRepository.findAll()).thenReturn(personajes);
		when(random.nextInt(anyInt())).thenReturn(0);

		ResultadoSobre resultado = sobreService.abrirSobre("goku");

		assertTrue(resultado.repetido());
		assertEquals(Rareza.LEGENDARIA.getReembolso(), resultado.reembolso());
		assertEquals(1000 - SobreService.PRECIO_SOBRE + Rareza.LEGENDARIA.getReembolso(), usuario.getMonedas());
		assertEquals(1, usuario.getColeccion().size());
	}

	@Test
	void abrirSobre_tiradaAlta_daUnPersonajeComun() {
		when(usuarioRepository.findByUsernameWithColeccion("goku")).thenReturn(Optional.of(usuario));
		when(personajeRepository.findAll()).thenReturn(personajes);
		when(random.nextInt(anyInt())).thenReturn(99, 0); // 99 sobre 100 cae en Común

		ResultadoSobre resultado = sobreService.abrirSobre("goku");

		assertEquals(Rareza.COMUN, resultado.rareza());
		assertEquals(4L, resultado.personaje().getId()); // el común más fuerte
	}

	@Test
	void abrirSobre_sinMonedasSuficientes_lanzaExcepcionYNoTocaLaColeccion() {
		usuario.setMonedas(SobreService.PRECIO_SOBRE - 1);
		when(usuarioRepository.findByUsernameWithColeccion("goku")).thenReturn(Optional.of(usuario));

		assertThrows(IllegalStateException.class, () -> sobreService.abrirSobre("goku"));

		assertEquals(SobreService.PRECIO_SOBRE - 1, usuario.getMonedas());
		assertTrue(usuario.getColeccion().isEmpty());
		verify(personajeRepository, never()).findAll();
	}

	@Test
	void abrirSobre_sinPersonajesEnBaseDeDatos_lanzaExcepcionSinCobrar() {
		when(usuarioRepository.findByUsernameWithColeccion("goku")).thenReturn(Optional.of(usuario));
		when(personajeRepository.findAll()).thenReturn(List.of());

		assertThrows(IllegalStateException.class, () -> sobreService.abrirSobre("goku"));
		assertEquals(1000, usuario.getMonedas());
	}

	@Test
	void agruparPorRareza_repartePorPercentilDeMaxKi() {
		Map<Rareza, List<Personaje>> porRareza = sobreService.agruparPorRareza(personajes);

		assertEquals(1, porRareza.get(Rareza.LEGENDARIA).size());
		assertEquals(2, porRareza.get(Rareza.EPICA).size());
		assertEquals(3, porRareza.get(Rareza.RARA).size());
		assertEquals(4, porRareza.get(Rareza.COMUN).size());
	}

	@Test
	void parsearKi_entiendeLosFormatosDeLaApi() {
		assertEquals(60_000_000d, SobreService.parsearKi("60.000.000"));
		assertEquals(90e24, SobreService.parsearKi("90 Septillion"), 1e10);
		assertEquals(2.5e9, SobreService.parsearKi("2.5 Billion"));
		assertEquals(0d, SobreService.parsearKi("Unknown"));
		assertEquals(0d, SobreService.parsearKi(null));
	}
}
