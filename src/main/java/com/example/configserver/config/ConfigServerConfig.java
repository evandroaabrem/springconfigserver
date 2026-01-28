package com.example.configserver.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
@ConfigurationProperties(prefix = "cliente")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ConfigServerConfig {
    private String nome;
    private String descricao;
    private String versao;
}

