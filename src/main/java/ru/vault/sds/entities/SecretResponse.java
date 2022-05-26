package ru.vault.sds.entities;

import lombok.Data;

import java.util.Map;

@Data
public class SecretResponse {

    private Map<String, Object> secretVault;
    private String error;

}
