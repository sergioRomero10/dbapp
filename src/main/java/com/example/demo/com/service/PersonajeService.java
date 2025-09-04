package com.example.demo.com.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.com.model.Personaje;
import com.example.demo.com.repository.PersonajeRepository;
/**
 * 🔹 RestTemplate vs WebClient
 *
 * En Spring Boot, existen dos formas principales de consumir APIs externas:
 * 
 * 1️⃣ RestTemplate
 * - Es el cliente HTTP tradicional de Spring.
 * - Bloqueante: la llamada HTTP espera a que la respuesta llegue antes de continuar.
 * - Fácil de usar y suficiente para aplicaciones simples o proyectos pequeños.
 * - A partir de Spring 5, RestTemplate está en **mantenimiento**, es decir, se sigue usando pero no se recomienda para nuevos proyectos.
 *
 * 2️⃣ WebClient
 * - Introducido en Spring 5 como parte de Spring WebFlux.
 * - No bloqueante (reactivo): permite manejar muchas solicitudes concurrentes sin bloquear hilos.
 * - Integración con Mono/Flux para programación reactiva.
 * - Mejor rendimiento en aplicaciones con muchas llamadas externas o APIs de alto tráfico.
 * - Permite timeout, retry, filtros y manejo de errores de forma más flexible.
 *
 * 💡 Recomendación:
 * - Para proyectos simples, RestTemplate sigue funcionando bien.
 * - Para proyectos modernos o de producción, especialmente con muchas llamadas externas o microservicios, usar WebClient.
 * - WebClient también se puede usar en aplicaciones tradicionales (Spring MVC) sin necesidad de WebFlux completo.
 *
 * Ejemplo básico con WebClient:
 *
 * WebClient client = WebClient.create("https://dragonball-api.com");
 * Mono<Personaje[]> response = client.get()
 *                                    .uri("/api/characters?limit=10")
 *                                    .retrieve()
 *                                    .bodyToMono(Personaje[].class);
 * 
 * response.subscribe(personajes -> {
 *     // Manejar personajes recibidos
 * });
 *
 */
/**
 * Servicio que maneja la lógica de negocio relacionada con los personajes.
 * 
 * Buenas prácticas aplicadas:
 * - Acceso a datos a través de PersonajeRepository.
 * - Lazy loading gestionado en entidades para evitar errores de Hibernate.
 * - Separación clara entre lógica de negocio (Service) y acceso a datos (Repository).
 */
@Service
public class PersonajeService {

    private final PersonajeRepository personajeRepository;

    // RestTemplate para consumir APIs externas (Dragon Ball API)
    private final RestTemplate restTemplate = new RestTemplate();

    // URL base de la API de Dragon Ball
    private static final String API_URL = "https://dragonball-api.com/api/characters?limit=10";

    /**
     * Constructor con inyección de dependencias de Spring
     */
    public PersonajeService(PersonajeRepository personajeRepository) {
        this.personajeRepository = personajeRepository;
    }

    /**
     * Obtener todos los personajes.
     * Si la base de datos está vacía, se cargan desde la API externa y luego se guardan.
     * 
     * @return lista de personajes
     */
    public List<Personaje> obtenerPersonajes() {
        List<Personaje> personajes = personajeRepository.findAll();
        if (personajes.isEmpty()) {
            cargarDesdeAPI(); // carga inicial desde API si BD vacía
            personajes = personajeRepository.findAll();
        }
        return personajes;
    }

    /**
     * Verifica si existe un personaje por su ID
     * 
     * @param personajeId ID del personaje
     * @return true si existe, false si no
     */
    public boolean existePersonaje(Long personajeId) {
        return personajeRepository.existsById(personajeId);
    }

    /**
     * Obtener un personaje por su ID
     * 
     * @param id ID del personaje
     * @return Optional con el personaje, vacío si no existe
     */
    public Optional<Personaje> obtenerPorId(Long id) {
        return personajeRepository.findById(id);
    }

    /**
     * Buscar personajes por nombre (case insensitive)
     * 
     * @param nombre nombre parcial o completo
     * @return lista de personajes que coinciden
     */
    public List<Personaje> buscarPorNombre(String nombre) {
        return personajeRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    /**
     * Buscar personajes por raza (case insensitive)
     * 
     * @param race raza parcial o completa
     * @return lista de personajes que coinciden
     */
    public List<Personaje> buscarPorRaza(String race) {
        return personajeRepository.findAll().stream()
                .filter(p -> p.getRace().toLowerCase().contains(race.toLowerCase()))
                .toList();
    }

    /**
     * Cargar personajes desde la API externa y guardarlos en la base de datos
     * 
     * Este método:
     * - Consume la API con RestTemplate
     * - Itera por cada personaje recibido y lo guarda en la BD
     * - Maneja paginación mediante el campo "next" de la API
     */
    private void cargarDesdeAPI() {
        String url = API_URL;
        boolean seguir = true;

        while (seguir) {
            // Obtener respuesta de la API
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

                // Guardar cada personaje en la base de datos
                for (Map<String, Object> item : items) {
                    Personaje p = new Personaje(
                            ((Number) item.get("id")).longValue(),
                            (String) item.get("name"),
                            (String) item.get("ki"),
                            (String) item.get("maxKi"),
                            (String) item.get("race"),
                            (String) item.get("gender"),
                            (String) item.get("description"),
                            (String) item.get("image"),
                            (String) item.get("affiliation"),
                            (String) item.get("deletedAt")
                    );
                    personajeRepository.save(p);
                }
            }

            // Manejo de paginación
            Map<String, Object> links = (Map<String, Object>) response.get("links");
            String next = links != null ? (String) links.get("next") : null;

            if (next == null || next.isEmpty()) {
                seguir = false; // no hay más páginas
            } else {
                url = next; // siguiente página
            }
        }
    }
}
