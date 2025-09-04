package com.example.demo.com.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.model.Usuario;
import com.example.demo.com.service.PersonajeService;
import com.example.demo.com.service.UsuarioService;

/**
 * Controlador principal para las vistas web de Personajes.
 *
 * Buenas prácticas:
 * 1️⃣ Separación de responsabilidades: el Controller solo maneja la interacción con la vista.
 * 2️⃣ Delegación de lógica al Service.
 * 3️⃣ Uso de Thymeleaf (Model) para pasar datos a la vista.
 */
@Controller
@RequestMapping("/vista") // Prefijo común para todas las rutas de este controlador
public class PersonajeWebController {

	private final PersonajeService personajeService;
	private final UsuarioService usuarioService;

	public PersonajeWebController(PersonajeService personajeService,UsuarioService usuarioService) {
		this.personajeService = personajeService;
		this.usuarioService = usuarioService;
	}

	/**
	 * Mostrar todos los personajes en la vista web.
	 *
	 * @param model Modelo para Thymeleaf
	 * @param usuario Usuario autenticado (inyección por Spring Security)
	 * @return nombre de la plantilla Thymeleaf "personajes.html"
	 */
	@GetMapping("/personajesweb")
	public String mostrarPersonajes(Model model,@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails usuario) {
		// Obtener todos los personajes desde la BD o la API
		List<Personaje> personajes = personajeService.obtenerPersonajes();
		model.addAttribute("personajes", personajes);

		if(usuario != null) {
			// Crear un Set con los IDs de los favoritos del usuario
			Set<Long> favoritosIds = usuario != null ?
					usuarioService.obtenerFavoritos(usuario.getUsername()).stream()
					.map(Personaje::getId)  // Extraemos solo los IDs
					.collect(Collectors.toSet())
					: Collections.emptySet();   // Si no hay usuario autenticado, lista vacía
			model.addAttribute("favoritosIds", favoritosIds);
		}
		// Retorna la plantilla Thymeleaf: src/main/resources/templates/personajes.html
		return "personajes";
	}

	/**
	 * Mostrar detalle de un personaje específico
	 *
	 * @param id ID del personaje
	 * @param model Modelo para Thymeleaf
	 * @return nombre de la plantilla "detalle-personaje.html"
	 */
	@GetMapping("/personajesweb/{id}")
	public String verDetallePersonaje(@PathVariable Long id, Model model) {
		// Buscar personaje por ID usando el service
		Personaje personaje = personajeService.obtenerPorId(id)
				.orElseThrow(() -> new RuntimeException("Personaje no encontrado"));
		model.addAttribute("personaje", personaje);

		return "detalle-personaje"; // plantilla Thymeleaf
	}

	@GetMapping("/favoritos")
	public String verFavoritosUsuario(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
	    if (userDetails == null) {
	        return "redirect:/login";
	    }

	    // Obtener la lista de personajes favoritos completos
	    List<Personaje> personajes = usuarioService.obtenerFavoritos(userDetails.getUsername());

	    // Crear un Set con los IDs de los favoritos para marcar ❤️ en la plantilla
	    Set<Long> favoritosIds = personajes.stream()
	                                      .map(Personaje::getId)
	                                      .collect(Collectors.toSet());

	    model.addAttribute("personajes", personajes);
	    model.addAttribute("favoritosIds", favoritosIds);

	    return "personajes"; // reutilizas la misma plantilla
	}


}
