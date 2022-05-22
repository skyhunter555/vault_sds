package ru.syntez.vault.sds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties("sds")
@Data
@RefreshScope
public class VaultConfig {

    private String key;
    private String cert;

}

