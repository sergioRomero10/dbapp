package com.example.demo.com.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;

import com.example.demo.com.model.EstadoSobres;
import com.example.demo.com.model.Personaje;
import com.example.demo.com.model.Rareza;
import com.example.demo.com.model.ResultadoSobre;
import com.example.demo.com.model.Usuario;
import com.example.demo.com.repository.PersonajeRepository;
import com.example.demo.com.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

/**
 * Lógica de apertura de sobres tipo gacha.
 *
 * 1. Se tira la rareza según su peso (Común 55%, Rara 28%, Épica 13%, Legendaria 4%).
 * 2. Se elige un personaje al azar dentro de esa rareza.
 * 3. Si ya estaba en la colección, se devuelve parte de las monedas según su rareza.
 *
 * La rareza de cada personaje se calcula por ranking de maxKi, así no hay que guardarla en BD.
 */
@Service
public class SobreService {

    public static final int PRECIO_SOBRE = 100;

    private static final Map<String, Double> MULTIPLICADORES = Map.ofEntries(
            Map.entry("thousand", 1e3), Map.entry("million", 1e6), Map.entry("billion", 1e9),
            Map.entry("trillion", 1e12), Map.entry("quadrillion", 1e15), Map.entry("quintillion", 1e18),
            Map.entry("sextillion", 1e21), Map.entry("septillion", 1e24), Map.entry("octillion", 1e27),
            Map.entry("nonillion", 1e30), Map.entry("decillion", 1e33), Map.entry("googol", 1e100),
            Map.entry("googolplex", Double.MAX_VALUE));

    private final UsuarioRepository usuarioRepository;
    private final PersonajeRepository personajeRepository;
    private final RandomGenerator random;

    public SobreService(UsuarioRepository usuarioRepository, PersonajeRepository personajeRepository,
            RandomGenerator random) {
        this.usuarioRepository = usuarioRepository;
        this.personajeRepository = personajeRepository;
        this.random = random;
    }

    @Transactional
    public ResultadoSobre abrirSobre(String username) {
        Usuario usuario = usuarioRepository.findByUsernameWithColeccion(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getMonedas() < PRECIO_SOBRE) {
            throw new IllegalStateException("No tienes suficientes monedas");
        }

        Map<Rareza, List<Personaje>> porRareza = agruparPorRareza(personajeRepository.findAll());
        if (porRareza.isEmpty()) {
            throw new IllegalStateException("No hay personajes disponibles todavía");
        }

        Rareza rareza = tirarRareza(porRareza);
        List<Personaje> candidatos = porRareza.get(rareza);
        Personaje personaje = candidatos.get(random.nextInt(candidatos.size()));

        // add() devuelve false si ya estaba en el Set -> personaje repetido
        boolean repetido = !usuario.getColeccion().add(personaje);
        int reembolso = repetido ? rareza.getReembolso() : 0;
        usuario.setMonedas(usuario.getMonedas() - PRECIO_SOBRE + reembolso);

        return new ResultadoSobre(personaje, rareza, repetido, reembolso);
    }

    @Transactional
    public EstadoSobres obtenerEstado(String username) {
        Usuario usuario = usuarioRepository.findByUsernameWithColeccion(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new EstadoSobres(usuario.getMonedas(), usuario.getColeccion().size(), personajeRepository.count());
    }

    /**
     * Ordena los personajes de más a menos poderoso y reparte las rarezas por percentil.
     */
    Map<Rareza, List<Personaje>> agruparPorRareza(List<Personaje> personajes) {
        List<Personaje> ordenados = personajes.stream()
                .sorted(Comparator.comparingDouble((Personaje p) -> parsearKi(p.getMaxKi())).reversed())
                .toList();

        Map<Rareza, List<Personaje>> porRareza = new EnumMap<>(Rareza.class);
        for (int i = 0; i < ordenados.size(); i++) {
            Rareza rareza = Rareza.porPercentil((double) i / ordenados.size());
            porRareza.computeIfAbsent(rareza, r -> new ArrayList<>()).add(ordenados.get(i));
        }
        return porRareza;
    }

    /**
     * Tirada ponderada: solo cuentan las rarezas que tienen algún personaje.
     */
    private Rareza tirarRareza(Map<Rareza, List<Personaje>> porRareza) {
        int pesoTotal = porRareza.keySet().stream().mapToInt(Rareza::getPeso).sum();
        int tirada = random.nextInt(pesoTotal);
        for (Rareza rareza : porRareza.keySet()) {
            tirada -= rareza.getPeso();
            if (tirada < 0) {
                return rareza;
            }
        }
        throw new IllegalStateException("Tirada fuera de rango");
    }

    /**
     * Convierte el ki de la API ("60.000.000", "90 Septillion", "Unknown"...) a número para poder ordenar.
     */
    static double parsearKi(String ki) {
        if (ki == null || ki.isBlank()) {
            return 0;
        }
        String[] partes = ki.trim().split("\\s+", 2);
        String numero;
        double multiplicador = 1;
        if (partes.length == 2) {
            // "2.5 Billion": el punto es decimal
            numero = partes[0].replace(',', '.');
            multiplicador = MULTIPLICADORES.getOrDefault(partes[1].toLowerCase(Locale.ROOT), 1d);
        } else {
            // "60.000.000": el punto es separador de miles
            numero = partes[0].replace(".", "").replace(",", "");
        }
        try {
            return Double.parseDouble(numero) * multiplicador;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
