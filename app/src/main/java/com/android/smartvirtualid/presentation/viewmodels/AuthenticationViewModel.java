package com.android.smartvirtualid.presentation.viewmodels;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AuthenticationViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<Person> personAuthenticateStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Organization> organizationAuthenticateStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> adminAuthenticateStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public AuthenticationViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void signInAsPerson(String email, String password) {
        SingleObserver<Person> singleObserver = databaseRepository.signInAsPerson(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Person>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Person person) {
                        personAuthenticateStateLiveData.setValue(person);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void signInAsOrganization(String email, String password) {
        SingleObserver<Organization> singleObserver = databaseRepository.signInAsOrganization(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Organization>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Organization organization) {
                        organizationAuthenticateStateLiveData.setValue(organization);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void signInAsAuthorityAdmin(String email, String password) {
        SingleObserver<Boolean> singleObserver = databaseRepository.signInAsAdmin(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        adminAuthenticateStateLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<Person> observePersonAuthenticateStateLiveDat() {
        return personAuthenticateStateLiveData;
    }

    public MediatorLiveData<Organization> observeOrganizationAuthenticateStateLiveDat() {
        return organizationAuthenticateStateLiveData;
    }

    public MediatorLiveData<Boolean> observeAdminAuthenticateStateLiveData() {
        return adminAuthenticateStateLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }

}
