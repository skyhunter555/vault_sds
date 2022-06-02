package ru.vault.sds.entities;

import java.util.Map;

public class SecretResponse {

    private Map<String, Object> secretVault;
    private String error;

    public Map<String, Object> getSecretVault() {
        return secretVault;
    }

    public void setSecretVault(Map<String, Object> secretVault) {
        this.secretVault = secretVault;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
