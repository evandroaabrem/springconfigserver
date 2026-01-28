package com.example.configserver.service;


import com.example.configserver.config.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final GitHubApiClient client;

    public String buscarUsuario(String username) {
        return client.getUser(username);
    }

    public String listarRepositorios(String username) {
        return client.listRepositories(username);
    }

}
