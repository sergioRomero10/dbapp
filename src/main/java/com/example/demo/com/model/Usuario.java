package com.example.demo.com.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa a un usuario en la aplicación.
 * 
 * Buenas prácticas aplicadas:
 * - equals/hashCode solo por ID para evitar problemas con colecciones lazy y Hibernate.
 * - @ManyToMany para la relación de favoritos con Personaje.
 * - Uso de HashSet para inicializar la colección y evitar NullPointerException.
 */
@Getter
@Setter
@Entity
@Table(name = "usuarios")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // solo ID define igualdad
public class Usuario {

    /**
     * Identificador único del usuario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Nombre de usuario (username)
     * No es único por ahora; se podría agregar @Column(unique = true) si deseas evitar duplicados
     */
    private String username;

    /**
     * Contraseña del usuario
     * Obligatorio (nullable = false)
     */
    @Column(nullable = false)
    private String password;

    /**
     * Personajes favoritos del usuario
     * Relación ManyToMany con la entidad Personaje
     * 
     * - @JoinTable define la tabla intermedia usuario_favoritos
     * - joinColumns indica la columna que apunta a Usuario
     * - inverseJoinColumns indica la columna que apunta a Personaje
     * - Se inicializa con HashSet para evitar NullPointerException y permitir add/remove
     */
    @ManyToMany
    @JoinTable(
        name = "usuario_favoritos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "personaje_id")
    )
    private Set<Personaje> favoritos = new HashSet<>();

    
    @Column(nullable = false)
    private Integer monedas = 1000;
    
    @ManyToMany
    @JoinTable(
        name = "usuario_coleccion",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "personaje_id")
    )
    private Set<Personaje> coleccion = new HashSet<>();
}
