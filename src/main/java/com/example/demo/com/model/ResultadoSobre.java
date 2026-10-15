package com.example.demo.com.model;

/**
 * Resultado de abrir un sobre: qué personaje ha salido, con qué rareza,
 * si era repetido y cuántas monedas se han devuelto por ello.
 */
public record ResultadoSobre(Personaje personaje, Rareza rareza, boolean repetido, int reembolso) {
}
