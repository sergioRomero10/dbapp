package com.example.demo.com.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad que representa un personaje de Dragon Ball en nuestra base de datos.
 * 
 * Buenas prácticas aplicadas:
 * - equals/hashCode solo por ID para evitar problemas con colecciones lazy y Hibernate.
 * - Colecciones ManyToMany son lazy por defecto para mejorar rendimiento.
 * - Campos que no se persisten directamente se marcan como @Transient.
 */
@Getter
@Setter
@Entity
@Table(name = "personaje") // Nombre de la tabla en la base de datos
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Solo ID define igualdad
public class Personaje {

    /**
     * Identificador único del personaje
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Incluir solo ID en equals/hashCode
    private Long id;

    /**
     * Nombre del personaje
     */
    private String name;

    /**
     * Valor de Ki actual
     */
    private String ki;

    /**
     * Valor de Ki máximo
     */
    private String maxKi;

    private String race;   // Raza del personaje
    private String gender; // Género del personaje

    /**
     * Descripción del personaje
     * Se aumenta la longitud para almacenar textos largos
     */
    @Column(length = 1000)
    private String description;

    private String image;       // URL o path de la imagen
    private String affiliation; // Afiliación (por ejemplo: Saiyan, Freezer Army, etc.)
    private String deletedAt;   // Fecha de eliminación lógica (nullable)

    /**
     * Transformaciones del personaje (por ejemplo: Super Saiyan)
     * No se persiste en la base de datos, se obtiene de la API externa.
     */
    @Transient
    private List<String> transformations;

    /**
     * Relación inversa con Usuario.
     * @ManyToMany mapeada desde la entidad Usuario.
     * Lazy loading por defecto evita cargar la colección innecesariamente.
     */
    @ManyToMany(mappedBy = "favoritos")
    private Set<Usuario> usuarios = new HashSet<>();

    /**
     * Constructor vacío obligatorio para JPA
     */
    protected Personaje() {}

    /**
     * Constructor completo (para uso manual o pruebas)
     */
    public Personaje(Long id, String name, String ki, String maxKi, String race, String gender,
                      String description, String image, String affiliation, String deletedAt) {
        this.id = id;
        this.name = name;
        this.ki = ki;
        this.maxKi = maxKi;
        this.race = race;
        this.gender = gender;
        this.description = description;
        this.image = image;
        this.affiliation = affiliation;
        this.deletedAt = deletedAt;
    }
}
