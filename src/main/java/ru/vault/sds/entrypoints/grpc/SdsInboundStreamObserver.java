package ru.vault.sds.entrypoints.grpc;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import io.envoyproxy.controlplane.cache.Resources;
import io.envoyproxy.envoy.config.core.v3.DataSource;
import io.envoyproxy.envoy.extensions.transport_sockets.tls.v3.TlsCertificate;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.extensions.transport_sockets.tls.v3.Secret;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.vault.sds.entities.SecretResponse;
import ru.vault.sds.entrypoints.http.VaultService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class SdsInboundStreamObserver implements StreamObserver<DiscoveryRequest> {

    private static Logger logger = LogManager.getLogger(SdsInboundStreamObserver.class);

    // use startTime for generating version string
    final long startTime = System.nanoTime();

    final StreamObserver<DiscoveryResponse> responseObserver;
    final SdsGRPCController sdsController;
    final Semaphore requestsCounter = new Semaphore(0);

    SdsInboundStreamObserver(StreamObserver<DiscoveryResponse> responseObserver, SdsGRPCController sdsController) {
        this.responseObserver = responseObserver;
        this.sdsController = sdsController;
    }

    //private void generateAsyncResponse(List<String> nameList) {
    //    checkNotNull(nameList, "nameList");
    //    if (!nameList.isEmpty()) {
    //        responseObserver.onNext(
    //                buildResponse(
    //                        sdsController.getCurrentVersion(),
    //                        sdsController.getLastRespondedNonce(),
    //                        nameList,
    //                        /* forcedAsync= */ true,
    //                        /* discoveryRequest= */ null));
    //    }
    //}

    @Override
    public void onNext(DiscoveryRequest discoveryRequest) {

        DiscoveryResponse discoveryResponse = buildResponse(discoveryRequest);
        if (discoveryResponse != null) {
            sdsController.setLastRequest(discoveryRequest);
            responseObserver.onNext(discoveryResponse);
        }
        requestsCounter.release();
    }

    @Override
    public void onError(Throwable t) {
        logger.error("onError ", t);
    }

    @Override
    public void onCompleted() {
        responseObserver.onCompleted();
    }

    private DiscoveryResponse buildResponse(DiscoveryRequest discoveryRequest) {
        //checkNotNull(discoveryRequest, "discoveryRequest");
        String requestVersion = discoveryRequest.getVersionInfo();
        String requestNonce = discoveryRequest.getResponseNonce();
        ProtocolStringList resourceNames = discoveryRequest.getResourceNamesList();
        return buildResponse(requestVersion, requestNonce, resourceNames, discoveryRequest);
    }

    private DiscoveryResponse buildResponse(
            String requestVersion,
            String requestNonce,
            List<String> resourceNames,
            DiscoveryRequest discoveryRequest) {
        //checkNotNull(resourceNames, "resourceNames");
        if (discoveryRequest != null && discoveryRequest.hasErrorDetail()) {
            sdsController.setLastRequest(discoveryRequest);
            return null;
        }
        // for stale version or nonce don't send a response
        //if (!StringUtils.isEmpty(requestVersion) && !requestVersion.equals(sdsController.getCurrentVersion())) {
        //    return null;
        //}
        //if (!StringUtils.isEmpty(requestNonce) && !requestNonce.equals(sdsController.getLastRespondedNonce())) {
        //    return null;
        //}
        // check if any new resources are being requested...
        //if (!forcedAsync && isSubset(resourceNames, lastResourceNames)) {
        //    if (discoveryRequest != null) {
        //        lastRequestOnlyForAck = discoveryRequest;
        //    }
        //    return null;
        //}

        final String version = generateVersionFromCurrentTime();
        DiscoveryResponse.Builder responseBuilder =
                DiscoveryResponse.newBuilder()
                        .setVersionInfo(version)
                        .setNonce(generateAndSaveNonce())
                        .setTypeUrl(Resources.V3.SECRET_TYPE_URL);

        for (String resourceName : resourceNames) {
            buildAndAddResource(responseBuilder, resourceName);
        }
        DiscoveryResponse response = responseBuilder.build();
        sdsController.setCurrentVersion(version);
        sdsController.setLastResponse(response);
        sdsController.setLastResourceNames(resourceNames);
        return response;
    }

    private String generateVersionFromCurrentTime() {
        return "" + ((System.nanoTime() - startTime) / 1000000L);
    }

    private String generateAndSaveNonce() {
        sdsController.setLastRespondedNonce(Long.toHexString(System.currentTimeMillis()));
        return sdsController.getLastRespondedNonce();
    }

    private void buildAndAddResource(
            DiscoveryResponse.Builder responseBuilder,
            String resourceName
    ) {
        VaultService vaultService = sdsController.getVaultService();

        SecretResponse secretResponse = null;
        if (sdsController.getTlsEnabled()) {
            try {
                secretResponse = vaultService.readSecretTLS(resourceName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            secretResponse = vaultService.readSecret(resourceName);
        }

        Secret secret = getOneTlsCertSecret(resourceName, secretResponse);
        if (secret != null) {
            ByteString data = secret.toByteString();
            Any anyValue = Any.newBuilder().setTypeUrl(Resources.V3.SECRET_TYPE_URL).setValue(data).build();
            responseBuilder.addResources(anyValue);
        }
    }

    private Secret getOneTlsCertSecret(String name, SecretResponse secretResponse) {

        Map<String, Object> secretMap = secretResponse.getSecretVault();
        if (secretMap.isEmpty()) {
            return null;
        }
        if (!secretMap.containsKey(sdsController.getSecretKeyName()) || !secretMap.containsKey(sdsController.getSecretCertName())) {
            return null;
        }
        String keyFileContent = secretMap.get(sdsController.getSecretKeyName()).toString();
        String certFileContent = secretMap.get(sdsController.getSecretCertName()).toString();

        TlsCertificate tlsCertificate =
                TlsCertificate.newBuilder()
                        .setPrivateKey(
                                DataSource.newBuilder()
                                        .setInlineBytes(ByteString.copyFromUtf8(keyFileContent))
                                        .build())
                        .setCertificateChain(
                                DataSource.newBuilder()
                                        .setInlineBytes(ByteString.copyFromUtf8(certFileContent))
                                        .build())
                        .build();
        return Secret.newBuilder().setName(name).setTlsCertificate(tlsCertificate).build();
    }
}
