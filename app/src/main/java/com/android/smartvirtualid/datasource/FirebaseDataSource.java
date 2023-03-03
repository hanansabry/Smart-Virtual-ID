package com.android.smartvirtualid.datasource;

import android.graphics.Bitmap;
import android.net.Uri;

import com.android.smartvirtualid.data.models.Member;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
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

    public Flowable<List<Person>> retrievePersonsData() {
        return Flowable.create(emitter -> {
            DatabaseReference personsRef = firebaseDatabase.getReference(Constants.PERSONS_NODE);
            personsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Person> personList = new ArrayList<>();
                    for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                        Person person = personSnapshot.getValue(Person.class);
                        person.setId(personSnapshot.getKey());
                        personList.add(person);
                    }
                    emitter.onNext(personList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Boolean> addNewOrganizationMember(Member member, String organizationId) {
        return Single.create(emitter -> {
            DatabaseReference memberRef = firebaseDatabase.getReference(Constants.ORGANIZATIONS_NODE)
                    .child(organizationId)
                    .child(Constants.MEMBERS);
            String memberId = memberRef.push().getKey();

            StorageReference qrCodeStorageReference = storageReference.child(Constants.QR_CODE_FOLDER + "/" + memberId + "/qr_code_" + System.currentTimeMillis() + Constants.IMAGE_FILE_TYPE);
            Bitmap qrCode = QRCode.from(memberId).withSize(1080, 1080).bitmap();
            //upload qrCode firstly to firebase storage
            UploadTask qrCodeUploadTask = saveQrCode(memberId, qrCode, qrCodeStorageReference);
            qrCodeUploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    emitter.onError(task.getException());
                } else {
                    qrCodeStorageReference.getDownloadUrl().addOnCompleteListener(uploadTask -> {
                        String downloadUrl = uploadTask.getResult().toString();
                        member.setQrCode(downloadUrl);
                        //save member to organization
                        memberRef.child(memberId).setValue(member)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        emitter.onSuccess(true);
                                        //save organization to person
                                        HashMap<String, Object> updateValues = new HashMap<>();
                                        updateValues.put("organization_id", organizationId);
                                        updateValues.put("member_id", memberId);
                                        updateValues.put("qr_code", member.getQrCode());
                                        updateValues.put("organization_name", member.getOrganizationName());
                                        firebaseDatabase.getReference(Constants.PERSONS_NODE)
                                                .child(member.getUserId())
                                                .child(Constants.ORGANIZATIONS_NODE)
                                                .push()
                                                .setValue(updateValues);
                                    } else {
                                        emitter.onSuccess(false);
                                    }
                                });
                    });
                }
                return null;
            });

        });
    }

    public Flowable<List<Member>> retrieveOrganizationMembers(String organizationId) {
        return Flowable.create(emitter -> {
            DatabaseReference organizationRef
                    = firebaseDatabase.getReference(Constants.ORGANIZATIONS_NODE)
                    .child(organizationId)
                    .child(Constants.MEMBERS);
            organizationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Member> memberList = new ArrayList<>();
                    for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                        Member member = memberSnapshot.getValue(Member.class);
                        member.setId(memberSnapshot.getKey());
                        memberList.add(member);
                    }
                    emitter.onNext(memberList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Boolean> updateOrganizationMember(Member member, String organizationId) {
        return Single.create(emitter -> {

            firebaseDatabase.getReference(Constants.ORGANIZATIONS_NODE)
                    .child(organizationId)
                    .child(Constants.MEMBERS)
                    .child(member.getId())
                    .setValue(member)
                    .addOnCompleteListener(task -> {
                        emitter.onSuccess(task.isSuccessful());
                    });
        });
    }

    public Single<Person> retrievePersonData(String userId) {
        return Single.create(emitter -> {

            firebaseDatabase.getReference(Constants.PERSONS_NODE)
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Person person = snapshot.getValue(Person.class);
                            person.setId(snapshot.getKey());
                            //get organizations of this person
                            List<HashMap<String, String>> organizationsList = new ArrayList<>();
                            for (DataSnapshot organizationSnapshot : snapshot.child(Constants.ORGANIZATIONS_NODE).getChildren()) {
                                HashMap<String, String> organizationsMap = (HashMap<String, String>) organizationSnapshot.getValue();
                                organizationsList.add(organizationsMap);
                            }
                            person.setOrganizationsList(organizationsList);
                            emitter.onSuccess(person);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            emitter.onError(error.toException());
                        }
                    });
        });
    }
}
