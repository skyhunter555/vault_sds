package ru.vault.sds.entrypoints.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.SimpleSessionManager;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.support.VaultResponse;
import ru.vault.sds.entities.SecretResponse;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

import javax.net.ssl.SSLContext;
import java.util.Objects;

@Service
public class VaultService {

    private static Logger LOG = LogManager.getLogger(VaultService.class);
    private static String tlsProtocol = "TLSv1.2";

    @Value("${vault.url}")
    private String vaultUrl = "127.0.0.1";

    @Value("${vault.port-http}")
    private Integer vaultPortHttp = 8200;

    @Value("${vault.port-https}")
    private Integer vaultPortHttps = 8201;

    @Value("${vault.token}")
    private String vaultToken = "";

    @Value("${vault.secret.path}")
    private String vaultSecretPath = "";

    @Value("${vault.tls.trust-store}")
    private Resource trustStore;

    @Value("${vault.tls.trust-store-password}")
    private String trustStorePassword;

    public SecretResponse readSecret(String resourceName) {

        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setHost(vaultUrl);
        vaultEndpoint.setPort(vaultPortHttp);
        vaultEndpoint.setScheme("http");

        // Authenticate
        VaultTemplate vaultTemplate = new VaultTemplate(
                vaultEndpoint,
                new TokenAuthentication(vaultToken));

        return getSecretFromVault(vaultTemplate, resourceName);
    }

    public SecretResponse readSecretTLS(String resourceName) throws Exception {

        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setHost(vaultUrl);
        vaultEndpoint.setPort(vaultPortHttps);
        vaultEndpoint.setScheme("https");

        SSLContext sslContext = new SSLContextBuilder()
                    .setProtocol(tlsProtocol)
                    .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
                    .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        VaultTemplate vaultTemplate = new VaultTemplate(
                vaultEndpoint,
                requestFactory,
                new SimpleSessionManager(new TokenAuthentication(vaultToken))
        );

        return getSecretFromVault(vaultTemplate, resourceName);
    }

    private SecretResponse getSecretFromVault(VaultTemplate vaultTemplate, String resourceName) {
        SecretResponse response = new SecretResponse();
        long startTime = System.currentTimeMillis();
        try {
            VaultResponse readResponse = vaultTemplate
                    .opsForKeyValue(vaultSecretPath, VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                    .get(resourceName);

            if (readResponse != null) {
                response.setSecretVault(Objects.requireNonNull(readResponse.getData()));
                response.setError("OK");
            }
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
