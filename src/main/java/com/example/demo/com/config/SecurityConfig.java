package com.example.demo.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
     * Webs que pueden mostrar la app dentro de un iframe (p. ej. el portfolio).
     * Se configura con la propiedad app.frame-ancestors (separadas por espacios).
     */
    @Value("${app.frame-ancestors:http://localhost:8099}")
    private String frameAncestors;

    /**
     * Configura las reglas de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🔹 Desactiva CSRF temporalmente (útil para pruebas, en producción revisar)
            .csrf(csrf -> csrf.disable())

            // 🔹 Permite mostrar la app en un iframe solo desde la propia app y el portfolio.
            //    Por defecto Spring Security envía X-Frame-Options: DENY y lo bloquea todo.
            .headers(headers -> headers
                    .frameOptions(frame -> frame.disable())
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives("frame-ancestors 'self' " + frameAncestors))
            )

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
