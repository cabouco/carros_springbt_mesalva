package com.carros.domain.upload;

import com.carros.api.upload.UploadInput;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

//https://firebase.google.com/docs/storage/admin/start
@Service
public class FirebaseStorageService {

    @PostConstruct
    private void init() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) {
            InputStream in = FirebaseStorageService.class.getResourceAsStream("/carros-be856-firebase-adminsdk-rprov-79b54f8d66.json");
//            FileInputStream in = new FileInputStream("/carros-be856-firebase-adminsdk-rprov-79b54f8d66.json");

            System.out.println(in);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .setStorageBucket("carros-be856.appspot.com")
                    .setDatabaseUrl("https://carros-be856.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    public String upload(UploadInput uploadInput) {

        Bucket bucket = StorageClient.getInstance().bucket();
        System.out.println(bucket);

//        Blob blob = bucket.create("nome.txt","Franklin Oliveira".getBytes(), "text/html");

        byte[] bytes = Base64.getDecoder().decode(uploadInput.getBase64());

        String fileName = uploadInput.getFileName();
        Blob blob = bucket.create(fileName,bytes,uploadInput.getMimeType());

        // Assina URL válida por N dias
//        URL signedUrl = blob.signUrl(3, TimeUnit.DAYS);

        // Deixa URL pública
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://firebasestorage.googleapis.com/v0/b/",bucket.getName(),fileName);
    }
}

// service firebase.storage {
// match /b/{bucket}/o {
// match /{allPaths=**} {
// allow read, write: if request.auth != null;
// }
// }
// }