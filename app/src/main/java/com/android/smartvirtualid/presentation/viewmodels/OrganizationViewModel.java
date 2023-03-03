package com.android.smartvirtualid.presentation.viewmodels;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Member;
import com.android.smartvirtualid.data.models.Organization;

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

public class OrganizationViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<Boolean> addOrganizationStateLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Member>> organizationMembersLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public OrganizationViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void addNewOrganization(Organization organization) {
        SingleObserver<Boolean> singleObserver = databaseRepository.addNewOrganization(organization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        addOrganizationStateLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void retrieveOrganizationMembers(String organizationId) {
        databaseRepository.retrieveOrganizationMembers(organizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<List<Member>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(List<Member> memberList) {
                        organizationMembersLiveData.setValue(memberList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        organizationMembersLiveData.setValue(null);
                    }
                });
    }

    public MediatorLiveData<Boolean> observeAddingOrganizationStateLiveData() {
        return addOrganizationStateLiveData;
    }

    public MediatorLiveData<List<Member>> observeOrganizationMembersLiveData() {
        return organizationMembersLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }
}
