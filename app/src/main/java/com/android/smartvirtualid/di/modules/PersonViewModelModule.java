package com.android.smartvirtualid.di.modules;

import com.android.smartvirtualid.di.ViewModelKey;
import com.android.smartvirtualid.presentation.viewmodels.AddMemberViewModel;
import com.android.smartvirtualid.presentation.viewmodels.PersonViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class PersonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PersonViewModel.class)
    public abstract ViewModel bindViewModel(PersonViewModel viewModel);
}
