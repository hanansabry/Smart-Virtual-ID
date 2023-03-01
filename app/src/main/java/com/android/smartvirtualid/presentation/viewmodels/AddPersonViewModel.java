package com.android.smartvirtualid.presentation.viewmodels;

import android.graphics.Bitmap;
import android.net.Uri;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Person;

import net.glxn.qrgen.android.QRCode;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPersonViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<String> uploadImageState = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> addPersonStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public AddPersonViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void uploadImageToFirebaseStorage(Uri imageUri) {
        String folderName = "FilesPhotos";
        SingleObserver<String> singleObserver = databaseRepository.uploadImageToFirebaseStorage(imageUri, folderName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(String downloadUrl) {
                        uploadImageState.setValue(downloadUrl);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void addNewPerson(Person person) {
        SingleObserver<Boolean> singleObserver = databaseRepository.addNewPerson(person)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        addPersonStateLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public Bitmap generateQrCode(String id) {
        return QRCode.from(id).withSize(1080, 1080).bitmap();
    }

    public boolean validatePersonData(String name,
                                      String birthdate,
                                      String nationality,
                                      String civilId,
                                      String disability,
                                      String residency,
                                      String email,
                                      String password,
                                      String phone,
                                      String martialStatus,
                                      String job,
                                      String gender,
                                      String photoUrl) {
        return !name.isEmpty()
                && !birthdate.isEmpty()
                && !nationality.isEmpty()
                && !civilId.isEmpty()
                && !disability.isEmpty()
                && !residency.isEmpty()
                && !email.isEmpty()
                && !password.isEmpty()
                && !phone.isEmpty()
                && !martialStatus.isEmpty()
                && !job.isEmpty()
                && !gender.isEmpty()
                && !photoUrl.isEmpty();
    }

    public boolean validateCivilId(String civilId) {
        return civilId.length() == 10;
    }

    public MediatorLiveData<String> observeUploadImageState() {
        return uploadImageState;
    }

    public MediatorLiveData<Boolean> observeAddPersonStateLiveData() {
        return addPersonStateLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }
}
