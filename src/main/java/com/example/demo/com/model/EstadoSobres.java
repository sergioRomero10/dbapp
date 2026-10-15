package com.example.demo.com.model;

/**
 * Datos que necesita la página de sobres: monedas del usuario y progreso de su colección.
 */
public record EstadoSobres(int monedas, int enColeccion, long totalPersonajes) {

    public int porcentajeColeccion() {
        return totalPersonajes == 0 ? 0 : (int) (enColeccion * 100 / totalPersonajes);
    }
}
