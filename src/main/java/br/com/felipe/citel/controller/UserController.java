package br.com.felipe.citel.controller;

import br.com.felipe.citel.model.User;
import br.com.felipe.citel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
        return ResponseEntity.ok(userEncontrado);
    }

    @PostMapping
    public ResponseEntity<List<User>> create(@RequestBody List<User> userRequests) {
        List<User> usersSalvos = userRequests.stream()
                .map(userService::create)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(usersSalvos);
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeCandidates(@RequestBody List<User> candidates) {
        Map<String, Object> results = userService.analyzeCandidates(candidates);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("Todos os usuários foram deletados com sucesso.");
    }
}
