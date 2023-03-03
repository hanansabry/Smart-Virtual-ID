package com.android.smartvirtualid.di.modules;

import com.android.smartvirtualid.di.ViewModelKey;
import com.android.smartvirtualid.presentation.viewmodels.OrganizationViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class OrganizationViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OrganizationViewModel.class)
    public abstract ViewModel bindViewModel(OrganizationViewModel viewModel);
}
