package com.example.demo.com.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.com.model.Rareza;
import com.example.demo.com.service.SobreService;

/**
 * Vista de apertura de sobres.
 *
 * El POST sigue el patrón Post/Redirect/Get: tras abrir el sobre se redirige al GET
 * y el resultado viaja como flash attribute. Así, recargar la página (F5) no vuelve
 * a enviar el formulario ni cobra otro sobre.
 */
@Controller
@RequestMapping("/vista/sobres")
public class AbrirSobreController {

    private final SobreService sobreService;

    public AbrirSobreController(SobreService sobreService) {
        this.sobreService = sobreService;
    }

    @GetMapping
    public String verSobres(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("estado", sobreService.obtenerEstado(userDetails.getUsername()));
        model.addAttribute("precio", SobreService.PRECIO_SOBRE);
        model.addAttribute("rarezas", Rareza.values());
        return "sobres";
    }

    @PostMapping("/abrir")
    public String abrirSobre(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("resultado", sobreService.abrirSobre(userDetails.getUsername()));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vista/sobres";
    }
}
