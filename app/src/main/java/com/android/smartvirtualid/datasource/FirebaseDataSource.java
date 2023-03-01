package com.android.smartvirtualid.datasource;

import android.graphics.Bitmap;
import android.net.Uri;

import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;

public class FirebaseDataSource {

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private SharedPreferencesDataSource sharedPreferencesDataSource;

    @Inject
    public FirebaseDataSource(StorageReference storageReference
            , FirebaseDatabase firebaseDatabase
            , FirebaseAuth firebaseAuth
            , SharedPreferencesDataSource sharedPreferencesDataSource) {
        this.storageReference = storageReference;
        this.firebaseDatabase = firebaseDatabase;
        this.firebaseAuth = firebaseAuth;
        this.sharedPreferencesDataSource = sharedPreferencesDataSource;
    }


    public void createUserWithEmailAndPassword(String email, String password, SingleEmitter<Boolean> emitter) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        emitter.onSuccess(true);
                    } else {
                        emitter.onError(task.getException());
                    }
                });
    }

    public Single<String> uploadImageToFirebaseStorage(Uri imageUri, String folderName) {
        return Single.create(emitter -> {
            StorageReference reference = storageReference.child(folderName + "/" + System.currentTimeMillis() + Constants.IMAGE_FILE_TYPE);
            UploadTask uploadFileTask = reference.putFile(imageUri);
            uploadFileTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    emitter.onError(task.getException());
                } else {
                    // Continue with the fileTask to get the download URL
                    reference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        String downloadUrl = task1.getResult().toString();
                        emitter.onSuccess(downloadUrl);
                    });
                }
                return reference.getDownloadUrl();
            });
        });
    }

    public Single<Boolean> addNewPerson(Person person) {
        return Single.create(emitter -> {
            DatabaseReference personsRef = firebaseDatabase.getReference(Constants.PERSONS_NODE);
            String personId = personsRef.push().getKey();

            StorageReference qrCodeStorageReference = storageReference.child(Constants.QR_CODE_FOLDER + "/" + personId + "/qr_code_" + System.currentTimeMillis() + Constants.IMAGE_FILE_TYPE);
            Bitmap qrCode = QRCode.from(personId).withSize(1080, 1080).bitmap();

            //upload qrCode firstly to firebase storage
            UploadTask qrCodeUploadTask = saveQrCode(personId, qrCode, qrCodeStorageReference);
            qrCodeUploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    emitter.onError(task.getException());
                } else {
                    // Continue with the fileTask to get the download URL
                    qrCodeStorageReference.getDownloadUrl().addOnCompleteListener(uploadTask -> {
                        String downloadUrl = uploadTask.getResult().toString();
                        person.setQrCodeUrl(downloadUrl);

                        //create user with email and password
                        firebaseAuth.createUserWithEmailAndPassword(person.getEmail(), person.getPassword())
                                .addOnCompleteListener(authTask -> {
                                    //save person in database
                                    if (authTask.isSuccessful()) {
                                        personsRef.push().setValue(person)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        emitter.onSuccess(true);
                                                    } else {
                                                        emitter.onSuccess(false);
                                                    }
                                                });
                                    } else {
                                        emitter.onError(authTask.getException());
                                    }
                                });
                    });
                }
                return null;
            });
        });
    }

    private UploadTask saveQrCode(String id, Bitmap qrCode, StorageReference qrCodeStorageReference) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCode.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return qrCodeStorageReference.putBytes(data);
    }
}
