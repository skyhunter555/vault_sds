package ru.syntez.vault.sds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sds")
@Data
public class VaultConfig {

    private String key;
    private String cert;

}

