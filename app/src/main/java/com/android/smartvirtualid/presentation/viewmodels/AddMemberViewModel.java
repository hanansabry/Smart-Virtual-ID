package com.android.smartvirtualid.presentation.viewmodels;

import android.net.Uri;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Member;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddMemberViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<String> uploadImageState = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> addMemberStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Person>> personsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public AddMemberViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void retrievePersonsData() {
        databaseRepository.retrievePersonsData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<List<Person>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(List<Person> diseaseList) {
                        personsLiveData.setValue(diseaseList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        personsLiveData.setValue(null);
                    }
                });
    }

    public void addNewOrganizationMember(Member member, String organizationId) {
        SingleObserver<Boolean> singleObserver = databaseRepository.addNewOrganizationMember(member, organizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        addMemberStateLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void updateOrganizationMember(Member member, String organizationId) {
        SingleObserver<Boolean> singleObserver = databaseRepository.updateOrganizationMember(member, organizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        addMemberStateLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void uploadImageToFirebaseStorage(Uri imageUri) {
        String folderName = "OrganizationMembersPhotos";
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

    public MediatorLiveData<String> observeUploadImageState() {
        return uploadImageState;
    }

    public MediatorLiveData<Boolean> observeAddMemberStateLiveData() {
        return addMemberStateLiveData;
    }

    public MediatorLiveData<List<Person>> observePersonsLiveData() {
        return personsLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }

}
