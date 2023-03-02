package com.android.smartvirtualid.datasource;

import android.graphics.Bitmap;
import android.net.Uri;

import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import androidx.annotation.NonNull;
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
            //create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(person.getEmail(), person.getPassword())
                    .addOnCompleteListener(authTask -> {
                        //save person in database
                        if (authTask.isSuccessful()) {
                            String personId = authTask.getResult().getUser().getUid();
                            StorageReference qrCodeStorageReference = storageReference.child(Constants.QR_CODE_FOLDER + "/" + personId + "/qr_code_" + System.currentTimeMillis() + Constants.IMAGE_FILE_TYPE);
                            Bitmap qrCode = QRCode.from(personId).withSize(1080, 1080).bitmap();

                            //upload qrCode firstly to firebase storage
                            UploadTask qrCodeUploadTask = saveQrCode(personId, qrCode, qrCodeStorageReference);
                            qrCodeUploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    emitter.onError(task.getException());
                                } else {
                                    qrCodeStorageReference.getDownloadUrl().addOnCompleteListener(uploadTask -> {
                                        String downloadUrl = uploadTask.getResult().toString();
                                        person.setQrCodeUrl(downloadUrl);
                                        personsRef.child(personId).setValue(person)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        emitter.onSuccess(true);
                                                    } else {
                                                        emitter.onSuccess(false);
                                                    }
                                                });
                                    });
                                }
                                return null;
                            });
                        } else {
                            emitter.onError(authTask.getException());
                        }
                    });
        });
    }

    private UploadTask saveQrCode(String id, Bitmap qrCode, StorageReference qrCodeStorageReference) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCode.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return qrCodeStorageReference.putBytes(data);
    }

    public Single<Boolean> addNewOrganization(Organization organization) {
        return Single.create(emitter -> {
            DatabaseReference organizationsRef = firebaseDatabase.getReference(Constants.ORGANIZATIONS_NODE);
            //create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(organization.getEmail(), organization.getPassword())
                    .addOnCompleteListener(authTask -> {
                        //save person in database
                        if (authTask.isSuccessful()) {
                            String id = authTask.getResult().getUser().getUid();
                            organizationsRef.child(id).setValue(organization)
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

    public Single<Person> signInAsPerson(String email, String password) {
        return Single.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();
                            DatabaseReference userRef = firebaseDatabase.getReference(Constants.PERSONS_NODE).child(userId);
                            userRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Person person = snapshot.getValue(Person.class);
                                            if (person != null) {
                                                person.setId(userId);
                                                emitter.onSuccess(person);
                                            } else {
                                                emitter.onError(new Throwable("error"));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            emitter.onError(new Throwable("error"));
                                        }
                                    });
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    public Single<Organization> signInAsOrganization(String email, String password) {
        return Single.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();
                            DatabaseReference userRef = firebaseDatabase.getReference(Constants.ORGANIZATIONS_NODE).child(userId);
                            userRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Organization organization = snapshot.getValue(Organization.class);
                                            if (organization != null) {
                                                organization.setId(userId);
                                                emitter.onSuccess(organization);
                                            } else {
                                                emitter.onError(new Throwable("error"));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            emitter.onError(new Throwable("error"));
                                        }
                                    });
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    public Single<Boolean> signInAsAuthorityAdmin(String email, String password) {
        return Single.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String adminId = task.getResult().getUser().getUid();
                            DatabaseReference userRef = firebaseDatabase.getReference("admin").child(adminId);
                            userRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String adminEmail = snapshot.child("email").getValue(String.class);
                                            String adminPassword = snapshot.child("password").getValue(String.class);
                                            if (email.equalsIgnoreCase(adminEmail)
                                                    && password.equals(adminPassword)) {
                                                emitter.onSuccess(true);
                                            } else {
                                                emitter.onError(new Throwable("error"));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            emitter.onError(new Throwable("error"));
                                        }
                                    });
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }
}
