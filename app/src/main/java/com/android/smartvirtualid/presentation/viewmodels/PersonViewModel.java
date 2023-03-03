package com.android.smartvirtualid.presentation.viewmodels;

import android.net.Uri;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Member;
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

public class PersonViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<Person> personLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public PersonViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void retrievePersonData(String userId) {
        SingleObserver<Person> singleObserver = databaseRepository.retrievePersonData(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Person>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Person person) {
                        personLiveData.setValue(person);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<Person> observePersonLiveData() {
        return personLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }

}
