package com.example.demo.com.model;

/**
 * Un personaje dentro del álbum de colección: su rareza y si el usuario ya lo tiene.
 */
public record CartaColeccion(Personaje personaje, Rareza rareza, boolean obtenida) {
}
