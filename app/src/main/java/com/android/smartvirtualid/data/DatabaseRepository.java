package com.android.smartvirtualid.data;

import android.net.Uri;

import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.datasource.FirebaseDataSource;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class DatabaseRepository {

    private final FirebaseDataSource firebaseDataSource;

    @Inject
    public DatabaseRepository(FirebaseDataSource firebaseDataSource) {
        this.firebaseDataSource = firebaseDataSource;
    }

    public Single<String> uploadImageToFirebaseStorage(Uri imageUri, String folderName) {
        return firebaseDataSource.uploadImageToFirebaseStorage(imageUri, folderName);
    }

    public Single<Boolean> addNewPerson(Person person) {
        return firebaseDataSource.addNewPerson(person);
    }

    public Single<Boolean> addNewOrganization(Organization organization) {
        return firebaseDataSource.addNewOrganization(organization);
    }

    public Single<Person> signInAsPerson(String email, String password) {
        return firebaseDataSource.signInAsPerson(email, password);
    }

    public Single<Organization> signInAsOrganization(String email, String password) {
        return firebaseDataSource.signInAsOrganization(email, password);
    }

    public Single<Boolean> signInAsAdmin(String email, String password) {
        return firebaseDataSource.signInAsAuthorityAdmin(email, password);
    }
}
