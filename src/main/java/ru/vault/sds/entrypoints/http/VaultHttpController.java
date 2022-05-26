package ru.vault.sds.entrypoints.http;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vault.sds.entities.SecretResponse;

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
    public SecretResponse readSecret(
        @ApiParam(
            value = "resourceName",
            required = true,
            defaultValue = "rnd-vault-sds"
        ) @RequestParam String resourceName
    ) {
        return vaultService.readSecret(resourceName);
    }

    // Read a secret tls
    @GetMapping("/read-secret-tls")
    @ApiOperation(value = "Read TLS secret", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SecretResponse readSecretTLS(
        @ApiParam(
           value = "resourceName",
           required = true,
           defaultValue = "rnd-vault-sds"
        ) @RequestParam String resourceName
    ) {
        try {
            return vaultService.readSecretTLS(resourceName);
        } catch (Exception e) {
            e.printStackTrace();
            return new SecretResponse();
        }
    }
}

