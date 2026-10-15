package com.example.demo.com.model;

import lombok.Getter;

/**
 * Rareza de un personaje dentro de los sobres.
 *
 * - peso: probabilidad (en %) de que salga esta rareza al abrir un sobre. Suman 100.
 * - reembolso: monedas que se devuelven si el personaje ya estaba en la colección.
 * - percentilMaximo: la rareza se asigna por ranking de maxKi (el 10% más fuerte es legendario, etc.).
 */
@Getter
public enum Rareza {

    LEGENDARIA("Legendaria", 4, 100, 0.10),
    EPICA("Épica", 13, 60, 0.30),
    RARA("Rara", 28, 40, 0.60),
    COMUN("Común", 55, 20, 1.00);

    private final String nombre;
    private final int peso;
    private final int reembolso;
    private final double percentilMaximo;

    Rareza(String nombre, int peso, int reembolso, double percentilMaximo) {
        this.nombre = nombre;
        this.peso = peso;
        this.reembolso = reembolso;
        this.percentilMaximo = percentilMaximo;
    }

    /**
     * @param percentil posición del personaje en el ranking de poder, de 0 (el más fuerte) a 1
     */
    public static Rareza porPercentil(double percentil) {
        for (Rareza rareza : values()) {
            if (percentil < rareza.percentilMaximo) {
                return rareza;
            }
        }
        return COMUN;
    }

    /** Nombre en minúsculas para usar como clase CSS (rareza-legendaria, rareza-comun...). */
    public String getCss() {
        return name().toLowerCase();
    }
}
