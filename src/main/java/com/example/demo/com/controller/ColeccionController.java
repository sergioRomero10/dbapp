package com.example.demo.com.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.com.model.CartaColeccion;
import com.example.demo.com.model.Rareza;
import com.example.demo.com.model.ResumenRareza;
import com.example.demo.com.service.SobreService;

/**
 * Álbum de colección: todos los personajes, con los no conseguidos en silueta.
 * El filtro por rareza se hace en el navegador (coleccion.js) para no recargar la página.
 */
@Controller
@RequestMapping("/vista/coleccion")
public class ColeccionController {

    private final SobreService sobreService;

    public ColeccionController(SobreService sobreService) {
        this.sobreService = sobreService;
    }

    @GetMapping
    public String verColeccion(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<CartaColeccion> cartas = sobreService.obtenerColeccion(userDetails.getUsername());

        // Se pasa una lista y no un Map<Rareza, Integer>: SpEL no sabe indexar un Map
        // con clave enum (${totales[r]} lanza "The target type java.lang.Enum does not refer to an enum")
        List<ResumenRareza> resumenes = Arrays.stream(Rareza.values())
                .map(rareza -> resumir(rareza, cartas))
                .filter(resumen -> resumen.total() > 0)
                .toList();

        model.addAttribute("cartas", cartas);
        model.addAttribute("resumenes", resumenes);
        model.addAttribute("totalObtenidas", cartas.stream().filter(CartaColeccion::obtenida).count());
        return "coleccion";
    }

    private ResumenRareza resumir(Rareza rareza, List<CartaColeccion> cartas) {
        List<CartaColeccion> deEstaRareza = cartas.stream().filter(c -> c.rareza() == rareza).toList();
        int obtenidas = (int) deEstaRareza.stream().filter(CartaColeccion::obtenida).count();
        return new ResumenRareza(rareza, obtenidas, deEstaRareza.size());
    }
}
