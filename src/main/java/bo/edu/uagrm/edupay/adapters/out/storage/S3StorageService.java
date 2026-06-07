package bo.edu.uagrm.edupay.adapters.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;
    private final boolean enabled;

    public S3StorageService(
            @Value("${aws.s3.bucket:edupay-scz-docs}") String bucket,
            @Value("${aws.region:us-east-1}") String region,
            @Value("${aws.access-key-id:}") String accessKeyId,
            @Value("${aws.secret-access-key:}") String secretAccessKey,
            @Value("${aws.s3.enabled:false}") boolean enabled) {
        this.bucket = bucket;
        this.enabled = enabled;

        if (enabled && !accessKeyId.isBlank()) {
            var credentials = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey));
            var awsRegion = Region.of(region);
            this.s3 = S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(credentials)
                    .build();
            this.presigner = S3Presigner.builder()
                    .region(awsRegion)
                    .credentialsProvider(credentials)
                    .build();
        } else {
            this.s3 = null;
            this.presigner = null;
        }
    }

    /**
     * Sube un archivo al bucket S3 y devuelve la clave (key) del objeto.
     *
     * @param key         Ruta dentro del bucket, e.g. "families/1/ci_20260605.pdf"
     * @param data        Contenido del archivo
     * @param contentType MIME type, e.g. "application/pdf"
     * @return La misma key usada, para guardar en BD
     */
    public String upload(String key, byte[] data, String contentType) {
        if (!enabled || s3 == null) {
            // En desarrollo sin credenciales AWS devuelve la key simulada
            return key;
        }
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(data));
        return key;
    }

    /**
     * Genera una URL prefirmada que expira en {@code expiryMinutes} minutos.
     * El frontend la usa para descargar/visualizar sin pasar por el backend.
     */
    public String generatePresignedUrl(String key, int expiryMinutes) {
        if (!enabled || presigner == null) {
            return "https://" + bucket + ".s3.amazonaws.com/" + key;
        }
        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiryMinutes))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Construye la key de S3 para un documento de familia.
     */
    public static String familyDocumentKey(Long familyId, String documentType, String originalFilename) {
        String ext = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".bin";
        return "families/" + familyId + "/docs/" + documentType.toLowerCase()
                + "_" + System.currentTimeMillis() + ext;
    }

    /**
     * Construye la key de S3 para una factura/recibo.
     */
    public static String invoiceKey(Long familyId, String periodCode) {
        return "invoices/" + periodCode.substring(0, 4) + "/" + periodCode + "/invoice_fam"
                + familyId + ".pdf";
    }
}
