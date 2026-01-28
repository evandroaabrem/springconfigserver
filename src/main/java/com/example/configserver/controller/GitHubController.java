package com.example.configserver.controller;

import com.example.configserver.config.GitHubApiClient;
import com.example.configserver.record.GitHubFileResponse;
import com.example.configserver.service.GitHubService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/github")
@AllArgsConstructor
public class GitHubController {

    private final GitHubService service;

    private final GitHubApiClient client;

    @GetMapping("/users/{username}")
    public ResponseEntity<String> getUser(@PathVariable String username) {
        return ResponseEntity.ok(service.buscarUsuario(username));
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<String> listRepos(@PathVariable String username) {
        return ResponseEntity.ok(service.listarRepositorios(username));
    }


    @GetMapping("/{profile}")
    public String getConfig(@PathVariable String profile) {
        var file = client.getConfigFile(profile);
        byte[] decoded = Base64.getDecoder()
                .decode(file.content().replaceAll("\\s", ""));

        return new String(decoded, StandardCharsets.UTF_8);
    }

    @PutMapping("/{profile}")
    public ResponseEntity<Void> updateConfig(
            @PathVariable String profile,
            @RequestBody String newContent
    ) {
        // 1️⃣ Buscar arquivo atual (para pegar o SHA)
        GitHubFileResponse file = client.getConfigFile(profile);

        // 2️⃣ Subir novo conteúdo (PUT no GitHub)
        client.updateConfigFile(
                profile,
                newContent,
                file.sha()
        );

        return ResponseEntity.noContent().build();
    }
}
