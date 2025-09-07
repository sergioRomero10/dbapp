package com.example.demo.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.com.service.CustomUserDetailsService;

/**
 * Configuración de Spring Security.
 * 
 * Buenas prácticas aplicadas:
 * - Uso de PasswordEncoder seguro (BCrypt) para almacenar contraseñas.
 * - Separación de rutas públicas y protegidas.
 * - Configuración de login y logout personalizada.
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Configura el AuthenticationManager usando nuestro UserDetailsService y el PasswordEncoder.
     * 
     * AuthenticationManager es responsable de autenticar usuarios con Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService) // integra nuestra lógica de carga de usuarios
                .passwordEncoder(passwordEncoder())      // aplica hash al comparar contraseñas
                .and()
                .build();
    }

    /**
     * Configura las reglas de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🔹 Desactiva CSRF temporalmente (útil para pruebas, en producción revisar)
            .csrf(csrf -> csrf.disable())

            // 🔹 Autorización de rutas
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/register",
                        "/login",
                        "/vista/personajesweb",
                        "/css/**",
                        "/js/**"
                    ).permitAll()   // rutas públicas
                    .anyRequest().authenticated() // resto protegido
            )

            // 🔹 Configuración de login
            .formLogin(form -> form
                    .loginPage("/login") // página de login personalizada
                    .successHandler((request, response, authentication) -> {
                        // Se ejecuta cuando el login es correcto
                        System.out.println("Usuario logeado: " + authentication.getName());
                        response.sendRedirect("/vista/personajesweb"); // redirige al inicio
                    })
                    .permitAll()
            )

            // 🔹 Configuración de logout
            .logout(logout -> logout
                    .logoutUrl("/logout")   // la URL que usas en Thymeleaf
                    .logoutSuccessUrl("/vista/personajesweb") // <-- aquí la redirección
                    .permitAll()
                );

        return http.build();
    }

    /**
     * PasswordEncoder seguro: BCrypt.
     * 
     * Buenas prácticas:
     * 1️⃣ Nunca almacenar contraseñas en texto plano.
     * 2️⃣ BCrypt incluye salt aleatorio automáticamente.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
