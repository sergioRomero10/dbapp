package com.example.demo.com.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.com.service.PersonajeService;
import com.example.demo.com.model.Personaje;

@RestController
public class PersonajeRestController {

    private final PersonajeService personajeService;

    public PersonajeRestController(PersonajeService personajeService) {
        this.personajeService = personajeService;
    }

    @GetMapping("/personajes")
    public List<Personaje> hello() {
        return personajeService.obtenerPersonajes();
    }

    @GetMapping("/personajes/{id}")
    public ResponseEntity<Personaje> obtenerPersonaje(@PathVariable Long id) {
        return personajeService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca personajes por nombre
    @GetMapping("/buscar/nombre")
    public List<Personaje> buscarPorNombre(@RequestParam String nombre) {
        return personajeService.buscarPorNombre(nombre);
    }

    // Busca personajes por raza
    @GetMapping("/buscar/raza")
    public List<Personaje> buscarPorRace(@RequestParam String race) {
        return personajeService.buscarPorRaza(race);
    }
}
