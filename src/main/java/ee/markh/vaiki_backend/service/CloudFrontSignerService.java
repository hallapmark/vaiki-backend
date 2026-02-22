package ee.markh.vaiki_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class CloudFrontSignerService {

    private final String keyPairId;
    private final PrivateKey privateKey;
    private final String distributionDomain;
    private final int defaultTtlSeconds;
    private final CloudFrontUtilities cloudFrontUtilities;

    public CloudFrontSignerService(
            @Value("${app.cloudfront.key-pair-id}") String keyPairId,
            @Value("${app.cloudfront.private-key-file:}") String privateKeyFile,
            @Value("${app.cloudfront.private-key-content:}") String privateKeyContent,
            @Value("${app.cloudfront.domain}") String distributionDomain,
            @Value("${app.cloudfront.url-ttl-seconds:3600}") int defaultTtlSeconds) throws Exception {

        this.keyPairId = keyPairId;
        this.privateKey = loadPrivateKey(privateKeyFile, privateKeyContent);
        this.distributionDomain = distributionDomain;
        this.defaultTtlSeconds = defaultTtlSeconds;
        this.cloudFrontUtilities = CloudFrontUtilities.create();
    }

    private static PrivateKey loadPrivateKey(String privateKeyFile, String privateKeyContent) throws Exception {

        String pemContent;
        if (privateKeyContent != null && !privateKeyContent.isBlank()) {
            pemContent = privateKeyContent;
        } else if (privateKeyFile != null && !privateKeyFile.isBlank()) {
            Path keyPath = Path.of(privateKeyFile);
            if (!Files.exists(keyPath) || !Files.isReadable(keyPath)) {
                throw new IllegalStateException(
                        "CloudFront private key file not readable: " + keyPath.toAbsolutePath()
                );
            }
            pemContent = Files.readString(keyPath);
        } else {
            throw new IllegalStateException(
                    "Neither app.cloudfront.private-key-file nor app.cloudfront.private-key-content is configured"
            );
        }
        return parsePrivateKey(pemContent);
    }

    private static PrivateKey parsePrivateKey(String pemContent) throws Exception {
        String stripped = pemContent
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getMimeDecoder().decode(stripped);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    /**
     * Generates a signed CloudFront URL for the given object path using a canned (predefined) policy.
     *
     * @param objectPath       The S3 object path (e.g., "/metropolis/master.m3u8")
     * @param expiresInSeconds TTL in seconds; if null, uses default from config
     * @return SignedUrlResult containing the signed URL and expiration timestamp
     */
    public SignedUrlResult getSignedUrl(String objectPath, Integer expiresInSeconds) throws Exception {
        int ttl = expiresInSeconds != null ? expiresInSeconds : defaultTtlSeconds;
        Instant expiresAt = Instant.now().plus(ttl, ChronoUnit.SECONDS);

        String resourceUrl = "https://" + distributionDomain + objectPath;

        CannedSignerRequest signerRequest = CannedSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .privateKey(privateKey)
                .keyPairId(keyPairId)
                .expirationDate(expiresAt)
                .build();

        try {
            SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest);
            return new SignedUrlResult(signedUrl.url(), expiresAt);
        } catch (Exception e) {
            throw new CloudFrontSigningException("Failed to sign CloudFront URL: " + resourceUrl, e);
        }
    }

    /**
     * Result record containing signed URL and expiration timestamp.
     */
    public record SignedUrlResult(String url, Instant expiresAt) {}

    /**
     * Runtime exception for CloudFront signing failures.
     */
    public static class CloudFrontSigningException extends RuntimeException {
        public CloudFrontSigningException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
