package br.com.bbrazsilveira.payment.v1.configuration.gcloud;

import com.google.cloud.storage.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


@Component
public class GCStorage {

    private Storage storage;

    public GCStorage() {
        storage = StorageOptions.getDefaultInstance().getService();
    }

    @Nullable
    public Blob get(String bucket, String object) {
        return storage.get(BlobId.of(bucket, object));
    }

    public Blob getFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String bucket, object;

            if (url.getHost().startsWith("storage.googleapis.com")) {
                String[] paths = url.getPath().substring(1).split("/", 2);
                bucket = paths[0];
                object = paths[1];
            } else {
                bucket = url.getHost().split("\\.", 2)[0];
                object = url.getPath().substring(1);
            }

            return get(bucket, object);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    public Blob create(String bucket, String object, String contentType, byte[] file, boolean isPublic) {
        return create(bucket, object, contentType, file, isPublic, null, null);
    }

    public Blob create(String bucket, String object, String contentType, byte[] file, boolean isPublic, DispositionType dispositionType, String fileName) {
        // Check if blob already exists
        Blob blob = get(bucket, object);
        if (blob != null && blob.exists()) {
            return blob;
        }

        // Use default content type when it is null
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Create blob builder
        BlobId blobId = BlobId.of(bucket, object);
        BlobInfo.Builder builder = BlobInfo.newBuilder(blobId).setContentType(contentType);

        // Update content disposition
        if (fileName != null) {
            if (fileName.split("\\.").length < 2) {
                throw new IllegalStateException(String.format("Bad format of attachment name {%s}. Missing file extension (.png, .jpg, .pdf, etc).", fileName));
            }

            builder.setContentDisposition(String.format("%s; filename=\"%s\"", dispositionType.toString().toLowerCase(), fileName));
        }

        // Update ACL
        if (isPublic) {
            builder.setAcl(new ArrayList<>(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))));
        }

        return storage.create(builder.build(), file);
    }

    public String getUrl(String bucket, String object) {
        return String.format("https://%s.storage.googleapis.com/%s", bucket, object);
    }

    public String getUrl(Blob blob) {
        return getUrl(blob.getBucket(), blob.getName());
    }

    public String getUrlSigned(Blob blob, int durationInMinutes) {
        return blob.signUrl(durationInMinutes, TimeUnit.MINUTES).toString();
    }

    public boolean download(File file, String url) {
        // Download template if it does not exist
        if (!file.exists()) {
            Blob blob = getFromUrl(url);
            blob.downloadTo(file.toPath());
            return true;
        }
        return false;
    }

    public enum DispositionType {
        INLINE, ATTACHMENT
    }
}