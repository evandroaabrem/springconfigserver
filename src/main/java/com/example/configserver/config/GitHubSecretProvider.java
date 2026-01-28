package com.example.configserver.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
public class GitHubSecretProvider {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GitHubSecretProvider() {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    public String getToken() {
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId("secret_spring")
                        .build()
        );

        try {
            JsonNode json = objectMapper.readTree(response.secretString());
            return json.get("github.token").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao ler token do GitHub", e);
        }
    }
}

