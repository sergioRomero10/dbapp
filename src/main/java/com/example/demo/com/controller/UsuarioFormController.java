package com.example.demo.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.com.model.Usuario;
import com.example.demo.com.service.UsuarioService;

/**
 * Controlador para el registro de usuarios.
 *
 * Buenas prácticas:
 * 1️⃣ Separación de responsabilidades: Controller maneja rutas y vistas, 
 *    el Service maneja la lógica de negocio (registro de usuarios).
 * 2️⃣ Uso de ModelAttribute para enlazar el formulario con la entidad Usuario.
 */
@Controller
@RequestMapping("/register") // Prefijo de rutas de registro
public class UsuarioFormController {

    private final UsuarioService usuarioService;

    public UsuarioFormController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Mostrar el formulario de registro.
     *
     * @param success Parámetro opcional para mostrar mensaje de éxito
     * @param model Modelo para Thymeleaf
     * @return Nombre de la plantilla "form-users.html"
     */
    @GetMapping("")
    public String formnRegister(@RequestParam(value = "success", required = false) String success, Model model) {
        // Crear un objeto vacío Usuario para que Thymeleaf lo enlace con el formulario
        model.addAttribute("usuario", new Usuario());

        // Si se pasó el parámetro success, agregar mensaje al modelo
        if (success != null) {
            model.addAttribute("successMessage", "Usuario registrado con éxito");
        }

        return "form-users"; // plantilla Thymeleaf
    }

    /**
     * Procesar el envío del formulario de registro.
     *
     * @param usuario Usuario creado a partir del formulario
     * @return Redirige al mismo formulario con parámetro ?success
     */
    @PostMapping("")
    public String procesarRegistro(@ModelAttribute Usuario usuario) {
        // Llamar al Service para guardar el usuario en la BD
        usuarioService.registrarUsuario(usuario);

        // Redirige con query param para mostrar mensaje de éxito
        return "redirect:/register?success";
    }
}
