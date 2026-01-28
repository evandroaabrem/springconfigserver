package com.example.configserver.record;

public record GitHubUpdateFileRequest(
        String message,
        String content,
        String sha
) {}