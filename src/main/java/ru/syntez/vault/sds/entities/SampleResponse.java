package ru.syntez.vault.sds.entities;

import lombok.Data;

import java.util.Map;

@Data
public class SampleResponse {

    private Map<String, Object> secretVault;
    private String error;

}
