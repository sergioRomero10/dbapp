package com.example.demo.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para la página de login.
 *
 * Buenas prácticas:
 * - Mantener la lógica del login simple en el Controller.
 * - Delegar toda la autenticación a Spring Security.
 */
@Controller
@RequestMapping("/login") // Prefijo de ruta para login
public class LoginController {

    /**
     * Muestra la página de login.
     *
     * @return Nombre de la plantilla Thymeleaf "login.html"
     */
    @GetMapping("")
    public String login() {
        // Thymeleaf buscará un archivo llamado login.html en resources/templates
        return "login";
    }
}
