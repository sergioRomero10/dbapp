package com.example.demo.com.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.com.service.UsuarioService;

/**
 * Controlador para agregar o quitar favoritos de un usuario.
 *
 * Buenas prácticas:
 * - Delegar la lógica de negocio al Service.
 * - Validar que el usuario esté autenticado antes de modificar datos.
 */
@Controller
public class FavoritoController {

    private final UsuarioService usuarioService;

    public FavoritoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Añade un personaje a los favoritos del usuario autenticado.
     * 
     * @param userDetails Usuario autenticado (proporcionado por Spring Security)
     * @param personajeId ID del personaje a agregar
     * @return Redirige a la vista de personajes
     */
    @GetMapping("/favorito/agregar/{personajeId}")
    public String agregarFavorito(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable Long personajeId) throws InterruptedException {

        // Si no hay usuario logeado, redirige al login
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Obtener el username del usuario autenticado
        String username = userDetails.getUsername();

        // Llamar al Service para agregar el personaje a favoritos
        usuarioService.agregarFavorito(username, personajeId);

        // Redirige a la vista principal de personajes
        return "redirect:/vista/personajesweb";
    }

    /**
     * Quita un personaje de los favoritos del usuario autenticado.
     * 
     * @param userDetails Usuario autenticado
     * @param personajeId ID del personaje a quitar
     * @return Redirige a la vista de personajes
     */
    @GetMapping("/favorito/quitar/{personajeId}")
    public String quitarFavorito(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long personajeId) {

        // Verificar autenticación
        if (userDetails == null) {
            return "redirect:/login"; // Alternativa: mostrar mensaje de error
        }

        String username = userDetails.getUsername();

        // Llamar al Service para eliminar el personaje de favoritos
        usuarioService.eliminarFavorito(username, personajeId);

        return "redirect:/vista/personajesweb";
    }
}
