package ru.syntez.vault.sds.entrypoints.http;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.syntez.vault.sds.entities.SampleResponse;

/**
 * Rest controller for vault
 *
 * @author Skyhunter
 * @date 19.05.2022
 */
@RestController
@Api(value = "sample-vault")
public class VaultHttpController {

    private final VaultService vaultService;

    public VaultHttpController(
            VaultService vaultService
    ) {
        this.vaultService = vaultService;
    }

    // Read a secret
    @GetMapping("/read-secret")
    @ApiOperation(value = "Read secret", produces = "application/json")
    public SampleResponse readSecret() {
        return vaultService.readSecret();
    }

    // Read a secret tls
    @GetMapping("/read-secret-tls")
    @ApiOperation(value = "Read TLS secret", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SampleResponse readSecretTLS() {
        return vaultService.readSecretTLS();
    }
}

