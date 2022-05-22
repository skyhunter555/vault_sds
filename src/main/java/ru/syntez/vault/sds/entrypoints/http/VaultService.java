package ru.syntez.vault.sds.entrypoints.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.vault.support.VaultResponse;
import ru.syntez.vault.sds.config.VaultConfig;
import ru.syntez.vault.sds.entities.SampleResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class VaultService {

    private final VaultConfig vaultConfig;

    public VaultService(
            VaultConfig vaultConfig
    ) {
        this.vaultConfig = vaultConfig;
    }

    private static Logger LOG = LogManager.getLogger(VaultService.class);

    public SampleResponse readSecret() {

        SampleResponse response = new SampleResponse();
        long startTime = System.currentTimeMillis();
        try {

            Map<String, Object> result = new HashMap<>();
            result.put("cert", vaultConfig.getCert());
            result.put("key", vaultConfig.getKey());
            VaultResponse readResponse = new VaultResponse();
            readResponse.setData(result);
            response.setSecretVault(Objects.requireNonNull(readResponse.getData()));
            response.setError("OK");
            //LOG.info(String.format("MessageResponse: %s", result));
        } catch (Exception e) {
            LOG.error(String.format("Error send message: %s", e.getMessage()));
            response.setError(e.getMessage());
        }

        long finishTime = System.currentTimeMillis();
        int receivedTotal = (int) (finishTime - startTime);
        LOG.info(String.format("Total time: %s ms.", receivedTotal));

        return response;

    }

    public SampleResponse readSecretTLS() {

        SampleResponse response = new SampleResponse();
        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("cert", vaultConfig.getCert());
            result.put("key", vaultConfig.getKey());
            VaultResponse readResponse = new VaultResponse();
            readResponse.setData(result);
            response.setSecretVault(Objects.requireNonNull(readResponse.getData()));
            response.setError("OK");
            //LOG.info(String.format("MessageResponse: %s", result));
        } catch (Exception e) {
            LOG.error(String.format("Error send message: %s", e.getMessage()));
            response.setError(e.getMessage());
        }

        long finishTime = System.currentTimeMillis();
        int receivedTotal = (int) (finishTime - startTime);
        LOG.info(String.format("Total time: %s ms.", receivedTotal));

        return response;
    }
}
