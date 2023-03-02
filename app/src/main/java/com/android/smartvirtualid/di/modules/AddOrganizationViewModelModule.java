package com.android.smartvirtualid.di.modules;

import com.android.smartvirtualid.di.ViewModelKey;
import com.android.smartvirtualid.presentation.viewmodels.AddOrganizationViewModel;
import com.android.smartvirtualid.presentation.viewmodels.AddPersonViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AddOrganizationViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddOrganizationViewModel.class)
    public abstract ViewModel bindViewModel(AddOrganizationViewModel viewModel);
}
