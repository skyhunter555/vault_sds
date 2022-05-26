package ru.vault.sds;

import io.envoyproxy.controlplane.cache.Resources;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.secret.v3.ReactorSecretDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.secret.v3.SecretDiscoveryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class GetSecretFromVaultTest {

    final long startTime = System.nanoTime();
    final String SECRET_NAME = "rnd-vault-sds";

    @Test
    public void fetchSecretsTest() throws Exception {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6501)
                .usePlaintext()
                .build();

        SecretDiscoveryServiceGrpc.SecretDiscoveryServiceBlockingStub secretDiscoveryService
                = SecretDiscoveryServiceGrpc.newBlockingStub(channel);

        DiscoveryResponse discoveryResponse = secretDiscoveryService.fetchSecrets(
                DiscoveryRequest.newBuilder()
                .setVersionInfo(generateVersionFromCurrentTime())
                .setTypeUrl(Resources.V3.SECRET_TYPE_URL)
                .setResponseNonce(generateResponseNonce())
                .addResourceNames(SECRET_NAME)
                .build());

        assertNotNull(discoveryResponse);

    }

    @Test
    public void streamSecretsTest() throws Exception {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6501)
                .usePlaintext()
                .build();

        ReactorSecretDiscoveryServiceGrpc.ReactorSecretDiscoveryServiceStub secretDiscoveryService
              = ReactorSecretDiscoveryServiceGrpc.newReactorStub(channel);

        DiscoveryRequest request = DiscoveryRequest.newBuilder()
                        .setVersionInfo(generateVersionFromCurrentTime())
                        .setTypeUrl(Resources.V3.SECRET_TYPE_URL)
                        .setResponseNonce(generateResponseNonce())
                        .addResourceNames(SECRET_NAME)
                        .build();

        DiscoveryResponse discoveryResponse = secretDiscoveryService.streamSecrets(Flux.just(request)).blockFirst();

        assertNotNull(discoveryResponse);

    }

    private String generateVersionFromCurrentTime() {
        return "" + ((System.nanoTime() - startTime) / 1000000L);
    }

    private String generateResponseNonce() {
        return Long.toHexString(System.currentTimeMillis());
    }
}
