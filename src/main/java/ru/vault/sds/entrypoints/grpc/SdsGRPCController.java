package ru.vault.sds.entrypoints.grpc;

import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.secret.v3.SecretDiscoveryServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import ru.vault.sds.entrypoints.http.VaultService;
import java.util.List;

@GrpcService
public class SdsGRPCController extends SecretDiscoveryServiceGrpc.SecretDiscoveryServiceImplBase {

    @Value("${vault.secret.key-name}")
    private String secretKeyName = "sds.key";

    @Value("${vault.secret.cert-name}")
    private String secretCertName = "sds.cert";

    @Value("${vault.tls.enabled}")
    private Boolean tlsEnabled = false;


    private final VaultService vaultService;

    public SdsGRPCController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    // we use startTime for generating version string.
    final long startTime = System.nanoTime();
    private SdsInboundStreamObserver inboundStreamObserver;
    private DiscoveryRequest lastRequest;
    private DiscoveryResponse lastResponse;
    private String currentVersion;
    private String lastRespondedNonce;
    private List<String> lastResourceNames;

    @Override
    public StreamObserver<DiscoveryRequest> streamSecrets(StreamObserver<DiscoveryResponse> responseObserver) {
        //checkNotNull(responseObserver, "responseObserver");
        inboundStreamObserver = new SdsInboundStreamObserver(responseObserver, this);
        return inboundStreamObserver;
    }

    @Override
    public StreamObserver<DeltaDiscoveryRequest> deltaSecrets(StreamObserver<DeltaDiscoveryResponse> responseObserver) {

        throw new UnsupportedOperationException("unary deltaSecrets not implemented!");
        /*
        return new StreamObserver<DeltaDiscoveryRequest>() {

            @Override
            public void onNext(DeltaDiscoveryRequest request) {
                DeltaDiscoveryResponse response = DeltaDiscoveryResponse.newBuilder()
                        //.setVersionInfo(request.getVersionInfo())
                        //.setTypeUrl(Resources.V3.SECRET_TYPE_URL)
                        //.addAllResources()
                        .setNonce(request.getResponseNonce())
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };*/
        //return createRequestHandler(responseObserver, Resources.V3.SECRET_TYPE_URL);
    }

    public void fetchSecrets(DiscoveryRequest request, StreamObserver<DiscoveryResponse> responseObserver) {
        //checkNotNull(responseObserver, "responseObserver");
        inboundStreamObserver = new SdsInboundStreamObserver(responseObserver, this);
        inboundStreamObserver.onNext(request);
        inboundStreamObserver.onCompleted();
    }

    public Boolean getTlsEnabled() {
        return tlsEnabled;
    }

    public VaultService getVaultService() {
        return vaultService;
    }

    public DiscoveryRequest getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(DiscoveryRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    public DiscoveryResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(DiscoveryResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLastRespondedNonce() {
        return lastRespondedNonce;
    }

    public void setLastRespondedNonce(String lastRespondedNonce) {
        this.lastRespondedNonce = lastRespondedNonce;
    }

    public List<String> getLastResourceNames() {
        return lastResourceNames;
    }

    public void setLastResourceNames(List<String> lastResourceNames) {
        this.lastResourceNames = lastResourceNames;
    }

    public String getSecretKeyName() {
        return secretKeyName;
    }

    public String getSecretCertName() {
        return secretCertName;
    }

}
