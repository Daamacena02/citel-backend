package br.com.felipe.citel.controller;
import br.com.felipe.citel.model.User;
import br.com.felipe.citel.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User userEncontrado = userService.findByid(id);
        if (userEncontrado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(userEncontrado);
    }

    @PostMapping
    public ResponseEntity<List<User>> create(@RequestBody List<User> userRequests) {
        List<User> usersSalvos = userRequests.stream()
                .map(userService::create)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(usersSalvos);
    }


    @GetMapping("/analyze/candidatos-por-estado")
    public ResponseEntity<Map<String, Long>> getCandidatosPorEstado() {
        List<User> candidates = userService.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HashMap<>());
        }
        Map<String, Long> candidatosPorEstado = userService.getCandidatosPorEstado(candidates);
        return ResponseEntity.ok(candidatosPorEstado);
    }

    @GetMapping("/analyze/imc-medio-por-faixa-etaria")
    public ResponseEntity<Map<String, Double>> getImcMedioPorFaixaEtaria() {
        List<User> candidates = userService.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HashMap<>());
        }
        Map<String, Double> imcMedioPorFaixaEtaria = userService.getImcMedioPorFaixaEtaria(candidates);
        return ResponseEntity.ok(imcMedioPorFaixaEtaria);
    }

    @GetMapping("/analyze/percentual-obesos")
    public ResponseEntity<Map<String, Double>> getPercentualObesos() {
        List<User> candidates = userService.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HashMap<>());
        }
        Map<String, Double> percentualObesos = userService.getPercentualObesos();
        return ResponseEntity.ok(percentualObesos);
    }

    @GetMapping("/analyze/media-idade-por-tipo-sanguineo")
    public ResponseEntity<Map<String, Double>> getMediaIdadePorTipoSanguineo() {
        List<User> candidates = userService.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HashMap<>());
        }
        Map<String, Double> mediaIdadePorTipoSanguineo = userService.getMediaIdadePorTipoSanguineo();
        return ResponseEntity.ok(mediaIdadePorTipoSanguineo);
    }

    @GetMapping("/analyze/possiveis-doadores")
    public ResponseEntity<Map<String, Long>> getPossiveisDoadores() {
        List<User> candidates = userService.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new HashMap<>());
        }
        Map<String, Long> possiveisDoadores = userService.getPossiveisDoadores();
        return ResponseEntity.ok(possiveisDoadores);
    }


    @DeleteMapping
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("Todos os usuários foram deletados com sucesso.");
    }
}
