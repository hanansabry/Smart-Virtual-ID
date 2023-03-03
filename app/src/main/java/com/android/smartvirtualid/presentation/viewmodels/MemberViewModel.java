package com.android.smartvirtualid.presentation.viewmodels;

import com.android.smartvirtualid.data.DatabaseRepository;
import com.android.smartvirtualid.data.models.Member;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MemberViewModel extends ViewModel {

    private final DatabaseRepository databaseRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MediatorLiveData<Member> memberLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public MemberViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void retrieveMemberData(String memberId, String organizationId) {
        SingleObserver<Member> singleObserver = databaseRepository.retrieveMemberData(memberId, organizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Member>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Member member) {
                        memberLiveData.setValue(member);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<Member> observeMemberLiveData() {
        return memberLiveData;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }

}
