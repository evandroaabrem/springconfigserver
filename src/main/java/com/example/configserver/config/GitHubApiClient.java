package com.example.configserver.config;


import com.example.configserver.record.GitHubFileResponse;
import com.example.configserver.record.GitHubUpdateFileRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class GitHubApiClient {

    private static final String BASE_URL = "https://api.github.com";

    private final RestTemplate restTemplate;
    private final GitHubSecretProvider secretProvider;

    public GitHubApiClient(GitHubSecretProvider secretProvider) {
        this.secretProvider = secretProvider;
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretProvider.getToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }

    public String getUser(String username) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/" + username,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    public String listRepositories(String username) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/" + username + "/repos",
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    public GitHubFileResponse getConfigFile(String profile) {
        String file = resolveFileByProfile(profile);

        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<GitHubFileResponse> response =
                restTemplate.exchange(
                        BASE_URL + "/repos/evandroaabrem/springconfig/contents/" + file,
                        HttpMethod.GET,
                        entity,
                        GitHubFileResponse.class
                );

        return response.getBody();
    }

    private String resolveFileByProfile(String profile) {
        return switch (profile) {
            case "dev" -> "cliente-service-dev.properties";
            case "uat" -> "cliente-service-uat.properties";
            case "prd" -> "cliente-service-prd.properties";
            default -> throw new IllegalArgumentException(
                    "Profile inválido: " + profile
            );
        };
    }

    public void updateConfigFile(
            String profile,
            String newContent,
            String sha
    ) {
        String file = resolveFileByProfile(profile);

        // 🔹 Base64 correto
        String encoded = Base64.getEncoder()
                .encodeToString(newContent.getBytes(StandardCharsets.UTF_8));

        GitHubUpdateFileRequest body =
                new GitHubUpdateFileRequest(
                        "Atualizando config " + profile,
                        encoded,
                        sha
                );

        HttpEntity<GitHubUpdateFileRequest> entity =
                new HttpEntity<>(body, defaultHeaders());

        restTemplate.exchange(
                BASE_URL + "/repos/evandroaabrem/springconfig/contents/" + file,
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }

}

