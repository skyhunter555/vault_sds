package ru.syntez.vault.sds.entrypoints.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.support.VaultResponse;
import ru.syntez.vault.sds.entities.SampleResponse;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

import java.util.Map;
import java.util.Objects;

@Service
public class VaultService {

    private static Logger LOG = LogManager.getLogger(VaultService.class);

    @Value("${vault.url}")
    private String vaultUrl = "127.0.0.1";

    @Value("${vault.port-http}")
    private Integer vaultPortHttp = 8200;

    @Value("${vault.port-https}")
    private Integer vaultPortHttps = 8201;

    public SampleResponse readSecret(String secretName) {

        VaultEndpoint vaultEndpoint = new VaultEndpoint();

        vaultEndpoint.setHost(vaultUrl);
        vaultEndpoint.setPort(vaultPortHttp);
        vaultEndpoint.setScheme("http");

        // Authenticate
        VaultTemplate vaultTemplate = new VaultTemplate(
                vaultEndpoint,
                new TokenAuthentication("root"));

        Map<String, Object> result = null;
        SampleResponse response = new SampleResponse();
        long startTime = System.currentTimeMillis();
            try {
                VaultResponse readResponse = vaultTemplate
                        .opsForKeyValue("ingress", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                        .get(secretName);

                if (readResponse != null) {
                    response.setSecretVault(Objects.requireNonNull(readResponse.getData()));
                    response.setError("OK");
                }
                LOG.info(String.format("MessageResponse: %s", result));
            } catch (Exception e) {
                LOG.error(String.format("Error send message: %s", e.getMessage()));
                response.setError(e.getMessage());
            }

        long finishTime = System.currentTimeMillis();
        int receivedTotal = (int) (finishTime - startTime);
        LOG.info(String.format("Total time: %s ms.", receivedTotal));

        return response;
    }

    public SampleResponse readSecretTLS(String secretName) {

        VaultEndpoint vaultEndpoint = new VaultEndpoint();

        vaultEndpoint.setHost(vaultUrl);
        vaultEndpoint.setPort(vaultPortHttps);
        vaultEndpoint.setScheme("https");

        //File caCertificate = new File(Settings.findWorkDir(), "ca/certs/ca.cert.pem");
        //SslConfiguration sslConfiguration = SslConfiguration.forTrustStore(SslConfiguration.KeyStoreConfiguration
        //        .of(new FileSystemResource(caCertificate)).withStoreType(SslConfiguration.PEM_KEYSTORE_TYPE));



        HttpClientBuilder httpClientBuilder = HttpClients.custom();
       // httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // add Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        HttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

       // CloseableHttpClient client = HttpClients.custom().setSSLContext(aValidSSLContext).build();
        //HttpClientBuilder httpClientBuilder = HttpClients.custom();
       // ClientHttpRequestFactory requestFactory = HttpComponents.usingHttpComponents(new ClientOptions(), sslConfiguration);

        VaultTemplate vaultTemplate = new VaultTemplate(vaultEndpoint, clientHttpRequestFactory);
        // Authenticate
        //VaultTemplate vaultTemplate = new VaultTemplate(
        //        vaultEndpoint,
        //        new TokenAuthentication("root"));

        Map<String, Object> result = null;
        SampleResponse response = new SampleResponse();
        long startTime = System.currentTimeMillis();
        try {
            VaultResponse readResponse = vaultTemplate
                    .opsForKeyValue("ingress", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                    .get(secretName);

            if (readResponse != null) {
                response.setSecretVault(Objects.requireNonNull(readResponse.getData()));
                response.setError("OK");
            }
            LOG.info(String.format("MessageResponse: %s", result));
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
